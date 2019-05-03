package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entity.SecKill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        this.jedisPool = new JedisPool(ip, port);
    }

    private RuntimeSchema<SecKill> schema = RuntimeSchema.createFrom(SecKill.class);

    public SecKill getSecKill(long seckillId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "seckill:" + seckillId;
            byte[] bytes = jedis.get(key.getBytes());
            // 采用自定义序列化
            if (bytes != null) {
                SecKill secKill = schema.newMessage();
                ProtostuffIOUtil.mergeFrom(bytes, secKill, schema);
                return secKill;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return null;
    }

    public String putSeckill(SecKill secKill) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "seckill:" + secKill.getSeckillId();
            byte[] bytes = ProtostuffIOUtil.toByteArray(secKill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            int timeout = 60 * 60;// 1小时
            return jedis.setex(key.getBytes(), timeout, bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
