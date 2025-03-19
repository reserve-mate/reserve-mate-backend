package com.reservemate.reserve_mate_backend.user.controller;

import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import com.reservemate.reserve_mate_backend.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  //회원등록
  @PostMapping("/register")
  public void registerUser(@RequestBody RequestUserDto requestUserDto){
    userService.registerUser(requestUserDto);
  }
}
