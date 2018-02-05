package com.nowcoder.util;

/**
 * Created by luziye on 2018/1/22.
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";

    public static String getLikeKey(int entityId, int entityType) {
        return BIZ_LIKE + SPLIT + entityId + SPLIT + entityType;
    }

    public static String getDisLikeKey(int entityId, int entityType) {
        return BIZ_DISLIKE + SPLIT + entityId + SPLIT + entityType;
    }

}
