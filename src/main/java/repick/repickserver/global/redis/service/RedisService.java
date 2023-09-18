package repick.repickserver.global.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void InsertToRedis(String key, String value, Long expireTime) {
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, expireTime, java.util.concurrent.TimeUnit.SECONDS);
    }

}
