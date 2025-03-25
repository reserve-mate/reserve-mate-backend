package com.reservemate.reserve_mate_backend.common.mail.util;

import java.time.Duration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisEmailAuthentication {

    private final StringRedisTemplate redisTemplate;

    public RedisEmailAuthentication(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //해당 이메일 인증 상태
    public String checkEmailAuthentication(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, "auth");
    }

    //해당 이메일로 인증 코드 가져오기
    public String getEmailAuthentication(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(key, "code");
    }

    //해당이메일,인증코드,인증상태,유효기간 저장
    public void setEmailAuthenticationExpire(String email, String code, Long expire) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(email, "code", code);
        hashOperations.put(email, "auth", "N");
        redisTemplate.expire(email, Duration.ofMinutes(expire));
    }

    //인증코드 인증완료시
    public void setEmailAuthenticationComplete(String email) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(email, "auth", "Y");
    }

    //삭제
    public void deleteEmailAuthentication(String key) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(key, "code");
        hashOperations.delete(key, "auth");
    }

}
