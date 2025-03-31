package com.reservemate.reserve_mate_backend.common.sms.util;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSmsAuthentication {

    private final StringRedisTemplate redisTemplate;

    public RedisSmsAuthentication(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    //인증번호 저장
    public void setSmsToken(String authCode, String phone, Long expireMinutes) {
        String key = "sms:auth:" + phone; // 키를 휴대폰 번호 기반으로 설정
        redisTemplate.opsForValue().set(key, authCode, Duration.ofMinutes(expireMinutes));
    }

    //휴대폰 번호로 인증번호 가져오기
    public String getSmsToken(String phone) {
        String key = "sms:auth:" + phone;
        return redisTemplate.opsForValue().get(key);
    }

    //휴대폰 번호로 인증번호 지우기
    public void deleteSmsToken(String phone) {
        String key = "sms:auth:" + phone;
        redisTemplate.delete(key);
    }
}
