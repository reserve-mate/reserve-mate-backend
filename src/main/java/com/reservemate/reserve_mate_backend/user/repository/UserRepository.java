package com.reservemate.reserve_mate_backend.user.repository;

import com.reservemate.reserve_mate_backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
