package com.nowcoder.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by luziye on 2018/1/22.
 */
@Component
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    public static void print(int index, Object object) {
        System.out.println(String.format("%d,%s", index, object.toString()));
    }


    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();
//
//        jedis.set("hello","world");
//        jedis.rename("hello","hello1");
//        print(1,jedis.get("hello1"));
//
//        jedis.setex("hello2",10,"world");
//
//        jedis.set("pv","100");
//        jedis.incrBy("pv",9);
//        print(2,jedis.get("pv"));


        String listNmae = "list";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listNmae, String.valueOf(i));
        }
        print(1, jedis.lrange(listNmae, 0, 10));
        print(2, jedis.llen(listNmae));
        print(3, jedis.lpop(listNmae));
        print(4, jedis.lrange(listNmae, 0, 10));
        print(5, jedis.llen(listNmae));
        print(6, jedis.lindex(listNmae, 5));
        print(7, jedis.linsert(listNmae, BinaryClient.LIST_POSITION.AFTER, "5", "xx"));
        print(8, jedis.linsert(listNmae, BinaryClient.LIST_POSITION.BEFORE, "5", "oo"));
        print(9, jedis.lrange(listNmae, 0, 20));

        String user = "user1";
        jedis.hset(user, "name", "luziye");
        jedis.hset(user, "age", "20");
        jedis.hset(user, "sex", "male");

        print(10, jedis.hget(user, "name"));

        print(11, jedis.hgetAll(user));
        print(12, jedis.hkeys(user));
        print(13, jedis.hvals(user));
        print(14, jedis.hexists(user, "age"));
        print(15, jedis.hexists(user, "family"));

        for (int i = 0; i < 10; i++) {
            jedis.sadd("slike1", String.valueOf(i));
            jedis.sadd("slike2", String.valueOf(i * 2));
        }
        print(16, jedis.sinter("slike1", "slike2"));
        print(17, jedis.sunion("slike1", "slike2"));
        print(18, jedis.sdiff("slike1", "slike2"));

        String rankKey = "rankKey";
        jedis.zadd(rankKey, 30, "ben");
        jedis.zadd(rankKey, 66, "alice");
        jedis.zadd(rankKey, 70, "lee");
        jedis.zadd(rankKey, 57, "lucy");
        jedis.zadd(rankKey, 90, "john");
        print(19, jedis.zscore(rankKey, "alice"));
        print(20, jedis.zcard(rankKey));
        print(21, jedis.zcount(rankKey, 50, 80));


    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);

    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key, Object obj) {
        set(key, JSON.toJSONString(obj));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }
}
