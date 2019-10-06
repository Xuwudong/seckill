package org.seckill.dto;

import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SecKillStateEnum;

/**
 * 封装秒杀执行后结果
 */
public class SecKillExecution {

    private long seckillId;


    // 秒杀执行结果状态
    private int state;

    private String stateInfo;

    private SuccessKilled successKilled;

    public SecKillExecution(long seckillId, SecKillStateEnum secKillStateEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = secKillStateEnum.getState();
        this.stateInfo = secKillStateEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    public SecKillExecution(long seckillId,  SecKillStateEnum secKillStateEnum) {
        this.seckillId = seckillId;
        this.state = secKillStateEnum.getState();
        this.stateInfo = secKillStateEnum.getStateInfo();
    }

    @Override
    public String toString() {
        return "SecKillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}
