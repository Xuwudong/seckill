package org.seckill.dao.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dao.SecKillDao;
import org.seckill.entity.SecKill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class RedisDaoTest {


    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SecKillDao secKillDao;

    private long id = 1001l;

    @Test
    public void getSecKill() {
        SecKill secKill = redisDao.getSecKill(id);
        if (secKill == null) {
            secKill = secKillDao.queryById(id);
            if (secKill == null) {
                System.out.println("不存在");
            } else {
                String res = redisDao.putSeckill(secKill);
                System.out.println(res);
                secKill = redisDao.getSecKill(id);
                System.out.println(secKill);
            }
        }
    }
}