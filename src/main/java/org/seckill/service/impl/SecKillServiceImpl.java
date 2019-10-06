package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SecKillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecKillServiceImpl implements SecKillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillDao secKillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;
    private final String salt = "fretgregrtht546t5y6fgbrtbI((**(734q078";

    public List<SecKill> getSecKillList() {
        return secKillDao.queryAll(0, 4);
    }

    public SecKill getSecKillById(long seckillId) {
        return secKillDao.queryById(seckillId);
    }

    @Transactional
    /**
     * 注解控制事务的优点
     * 1、开发人员约定一致，明确标注事务方法的编程风格
     * 2、保证事务方法的执行时间尽可能短，不要穿插其他网络操作rpc/http请求或者剥离到事务方法外部
     * 3、不是所有方法都需要事务，
     */
    public Exposer exportSecKillUrl(long seckillId) {
        // 缓存优化，超时的基础上维护唯一性
        SecKill secKill = redisDao.getSecKill(seckillId);
        if (secKill == null) {
            secKill = secKillDao.queryById(seckillId);
            if (secKill == null) {
                return new Exposer(false, seckillId);
            } else {
                redisDao.putSeckill(secKill);
            }
        }
        Date startTime = secKill.getStartTime();
        Date endTime = secKill.getEndTime();
        Date now = new Date();
        if (now.getTime() < startTime.getTime() || now.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, now.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    public SecKillExecution executeSecKill(long seckillId, long userPhone, String md5) throws SecKillException {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SecKillException("seckill data rewrite");
        }
        Date now = new Date();
        try {
            // 减库存
            int updateCount = secKillDao.reduceNumber(seckillId, now);
            if (updateCount <= 0) {
                throw new SecKillCloseException("seckill is closed");
            } else {
                // 记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount == 0) {
                    // 重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SecKillExecution(seckillId, SecKillStateEnum.SUCCESS, successKilled);
                }
            }
        } catch (SecKillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 所有编译期异常 转化为运行时异常
            throw new SecKillException("seckill inner error:" + e.getMessage());
        }
    }


    /**
     * 执行秒杀接口
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SecKillException
     */
    public SecKillExecution executeSecKillByProducure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SecKillExecution(seckillId, SecKillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        try {
            secKillDao.killByProceduce(map);
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SecKillExecution(seckillId, SecKillStateEnum.SUCCESS, sk);
            } else {
                return new SecKillExecution(seckillId, SecKillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SecKillExecution(seckillId, SecKillStateEnum.INNER_ERROR);
        }
    }
}
