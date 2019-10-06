package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SecKill;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SecKillDao {
    /**
     * 减库存
     *
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 查询一个
     *
     * @param seckillId
     * @return
     */
    SecKill queryById(long seckillId);

    /**
     * 查询列表
     *
     * @param offset
     * @param limit
     * @return
     */
    List<SecKill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 通过存储过程执行秒杀
     *
     * @param paramMap
     */
    void killByProceduce(Map<String, Object> paramMap);

}
