package com.nowcoder.service;

import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by luziye on 2018/1/22.
 */
@Service
public class LikeService {

    @Autowired
    private JedisAdapter jedisAdapter;


    public int getLikeStatus(int userId, int entityId, int entityType) {
        String like = RedisKeyUtil.getLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(like, String.valueOf(userId))) {
            return 1;//1喜欢 -1不喜欢 0默认不操作
        }
        String dislike = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.sismember(dislike, String.valueOf(userId)) ? -1 : 0;
    }

    public long like(int userId, int entityId, int entityType) {
        String like = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(like, String.valueOf(userId));
        String dislike = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(dislike, String.valueOf(userId));
        return jedisAdapter.scard(like);
    }

    public long dislike(int userId, int entityId, int entityType) {
        String dislike = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(dislike, String.valueOf(userId));
        String like = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(like, String.valueOf(userId));

        return jedisAdapter.scard(dislike);
    }
}
