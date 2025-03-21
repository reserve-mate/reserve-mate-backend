package com.reservemate.reserve_mate_backend.common.auth.repository;

import com.reservemate.reserve_mate_backend.common.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshRepository extends CrudRepository<RefreshToken, String> {

}
