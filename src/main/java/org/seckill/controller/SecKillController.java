package org.seckill.controller;

import com.sun.org.apache.regexp.internal.RE;
import org.seckill.dao.SecKillResult;
import org.seckill.dto.Exposer;
import org.seckill.dto.SecKillExecution;
import org.seckill.entity.SecKill;
import org.seckill.enums.SecKillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SecKillCloseException;
import org.seckill.exception.SecKillException;
import org.seckill.service.SecKillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/seckills")
public class SecKillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SecKillService secKillService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        List<SecKill> list = secKillService.getSecKillList();
        model.addAttribute("list", list);
        return "list";
    }

    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckills/list";
        }
        SecKill secKill = secKillService.getSecKillById(seckillId);
        if (secKill == null) {
            return "forward:/seckills/list";
        }
        model.addAttribute("secKill", secKill);
        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public SecKillResult<Exposer> exposer(@PathVariable Long seckillId) {
        SecKillResult<Exposer> result;
        try {
            Exposer exposer = secKillService.exportSecKillUrl(seckillId);
            result = new SecKillResult<Exposer>(true, exposer);
        } catch (SecKillException e) {
            logger.error(e.getMessage(), e);
            result = new SecKillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/{md5}/execute",
            method = RequestMethod.POST,
            produces = "application/json;charset=utf-8"
    )
    @ResponseBody
    public SecKillResult<SecKillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone", required = false) Long userPhone, @PathVariable("md5") String md5) {
        SecKillResult<SecKillExecution> result;

        // spring mvc valid
        if (userPhone == null) {
            return new SecKillResult<SecKillExecution>(false, "未注册");
        }
        try {
            SecKillExecution secKillExecution = secKillService.executeSecKill(seckillId, userPhone, md5);
            result = new SecKillResult<SecKillExecution>(true, secKillExecution);
        } catch (RepeatKillException e) {
            SecKillExecution secKillExecution = new SecKillExecution(seckillId, SecKillStateEnum.REPEAT_KILL);
            return new SecKillResult<SecKillExecution>(true, secKillExecution);
        } catch (SecKillCloseException e) {
            SecKillExecution secKillExecution = new SecKillExecution(seckillId, SecKillStateEnum.END);
            return new SecKillResult<SecKillExecution>(true, secKillExecution);
        } catch (SecKillException e) {
            logger.error(e.getMessage(), e);
            SecKillExecution secKillExecution = new SecKillExecution(seckillId, SecKillStateEnum.INNER_ERROR);
            return new SecKillResult<SecKillExecution>(false, secKillExecution);
        }
        return result;
    }

    @RequestMapping("/time/now")
    @ResponseBody
    public SecKillResult<Long> time() {
        Date now = new Date();
        return new SecKillResult<Long>(true, now.getTime());
    }
}
