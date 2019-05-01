package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml", "classpath:spring/spring-server.xml"})
public class SecKillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;

    @Test
    public void testGetSecKillList() {
        List<SecKill> secKillList = secKillService.getSecKillList();
        logger.info("list={}", secKillList);
    }

    @Test
    public void testGetSecKillById() {
        long id = 1000;
        SecKill secKill = secKillService.getSecKillById(id);
        logger.info("secKill={}", secKill);
    }

    // 集成测试
    @Test
    public void testSecKillLogic() {
        Exposer exposer = secKillService.exportSecKillUrl(1000);
        if (exposer.isExposed()) {
            try {
                SecKillExecution secKillExecution = secKillService.executeSecKill(1000, 17813294100L, exposer.getMd5());
                logger.info("secKillExecution={}", secKillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SecKillCloseException e) {
                logger.error(e.getMessage());
            }
        } else {
            // 秒杀未开启
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void testExecuteSecKill() {
        try {
            SecKillExecution secKillExecution = secKillService.executeSecKill(1000, 18813294100L, "7ee3d106ee867cea5d6218f627d9f103");
            logger.info("secKillExecution={}", secKillExecution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage());
        } catch (SecKillCloseException e) {
            logger.error(e.getMessage());
        }
    }

}