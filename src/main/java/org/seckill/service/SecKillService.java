package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.SecKillException;

import java.util.List;

/**
 * 站在使用者角度设计接口
 * 方法名，方法参数，返回类型
 */
public interface SecKillService {

    List<SecKill> getSecKillList();

    SecKill getSecKillById(long seckillId);

    /**
     * 输出秒杀接口地址
     *
     * @param seckillId
     */
    Exposer exportSecKillUrl(long seckillId);

    /**
     * 执行秒杀接口
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SecKillException
     */
    SecKillExecution executeSecKill(long seckillId, long userPhone, String md5)
            throws SecKillException;

    /**
     * 执行秒杀接口
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SecKillException
     */
    SecKillExecution executeSecKillByProducure(long seckillId, long userPhone, String md5);
}

