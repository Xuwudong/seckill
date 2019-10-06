package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SecKill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置spring和junit整合，Junit启动时加载spring容器
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SecKillDaoTest {

    @Resource
    private SecKillDao seckillDao;


    @Test
    public void testQueryById() throws Exception {
        long id = 1000;
        SecKill secKill = seckillDao.queryById(id);
        System.out.println(secKill.getName());
        System.out.println(secKill);
    }

    @Test
    public void testQueryAll() throws Exception {
        List<SecKill> seckills = seckillDao.queryAll(0, 100);
        for (SecKill secKill : seckills) {
            System.out.println(secKill);
        }
    }


    @Test
    public void testReduceNumber() throws Exception {
        Date killTime=  new Date();
        int updateCount = seckillDao.reduceNumber(1000,killTime);
        System.out.println("updateCount: " + updateCount);
    }
}