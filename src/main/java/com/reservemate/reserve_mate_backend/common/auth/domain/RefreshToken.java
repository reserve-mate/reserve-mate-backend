package com.reservemate.reserve_mate_backend.common.auth.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@NoArgsConstructor
@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {

    @Id
    private String refresh;
    private Long id;
    private String expiration;

    @Builder(toBuilder = true)
    public RefreshToken(String refresh, Long id, String expiration) {
        this.refresh = refresh;
        this.id = id;
        this.expiration = expiration;
    }
}
