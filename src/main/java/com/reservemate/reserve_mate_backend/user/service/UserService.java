package com.reservemate.reserve_mate_backend.user.service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.mail.service.MailService;
import com.reservemate.reserve_mate_backend.common.mail.util.RedisEmailAuthentication;
import com.reservemate.reserve_mate_backend.common.sms.dto.RequestFindEmailDto;
import com.reservemate.reserve_mate_backend.common.sms.util.RedisSmsAuthentication;
import com.reservemate.reserve_mate_backend.common.sms.util.SmsUtil;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import com.reservemate.reserve_mate_backend.user.dto.response.ResponseUserDto;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisEmailAuthentication redisEmailAuthentication;
    private final SmsUtil smsUtil;
    private final RedisSmsAuthentication redisSmsAuthentication;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
        MailService mailService, RedisEmailAuthentication redisEmailAuthentication, SmsUtil smsUtil,
        RedisSmsAuthentication redisSmsAuthentication, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.redisEmailAuthentication = redisEmailAuthentication;
        this.smsUtil = smsUtil;
        this.redisSmsAuthentication = redisSmsAuthentication;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<ResponseUserDto> registerUser(RequestUserDto requestUserDto) {
        // 이메일 중복체크
        boolean emailExists = userRepository.existsByEmail(requestUserDto.getEmail());

        if (emailExists) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        User user = User.builder()
            .name(requestUserDto.getName())
            .email(requestUserDto.getEmail())
            .password(passwordEncoder.encode(requestUserDto.getPassword())) // 암호화
            .phone(requestUserDto.getPhone())
            .profileImage(requestUserDto.getProfileImage())
            .role(UserRole.ROLE_USER) // 회원가입 하는 경우 유저로 셋팅
            .build();

        try {
            //save
            User savedUser = userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseUserDto(savedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        user.delete();
    }

    @Transactional
    public void updateUser(Long id, RequestUserDto requestUserDto) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        user.updateUser(
            requestUserDto.getName(),
            requestUserDto.getEmail(),
            passwordEncoder.encode(requestUserDto.getPassword()),
            requestUserDto.getPhone(),
            requestUserDto.getProfileImage());
    }

    public void sendResetPasswordEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 없습니다."));

        mailService.sendResetPasswordEmail(user.getEmail());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        //redis에 uuid 있는지 확인
        String email = redisEmailAuthentication.getEmailByResetPasswordToken(token);
        if (email == null) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));

        user.updatePassword(passwordEncoder.encode(newPassword));
        redisEmailAuthentication.deleteResetPasswordToken(token);
    }

    public String sendAuthCodeForFindEmail(RequestFindEmailDto findEmailRequestDto) {
        User user = userRepository.findByPhone(findEmailRequestDto.getPhone())
            .orElseThrow(() -> new IllegalArgumentException("입력한 휴대폰 번호와 일치하는 사용자가 없습니다."));
        //인증번호 생성
        String authCode = generateAuthCode();
        //하이픈 제거
        String phoneNumber = user.getPhone().replaceAll("-", "");

        //sms 발송
        SingleMessageSentResponse response = smsUtil.sendOne(user.getPhone(), authCode);
        if (response != null && "2000".equals(response.getStatusCode())) {
            //인증번호 redis 저장 유효기간 5분
            redisSmsAuthentication.setSmsToken(authCode, findEmailRequestDto.getPhone(), 5L);
            return "인증번호가 전송되었습니다.";
        } else {
            return "인증번호 전송에 실패하였습니다.";
        }
    }

    private String generateAuthCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    public String verifyAuthCodeAndReturnEmail(String authCode, String phone) {
        //Redis에서 authCode 가져옴
        String storedAuthCode = redisSmsAuthentication.getSmsToken(phone);
        //인증번호 없거나 일치하지 않는경우
        if (storedAuthCode == null || !storedAuthCode.equals(authCode)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않거나 만료되었습니다.");
        }
        //인증번호 가져온 경우 Redis에서 인증번호 삭제
        redisSmsAuthentication.deleteSmsToken(phone);
        //등록된 휴대폰 번호로 이메일 조회
        User user = userRepository.findByPhone(phone).orElseThrow(() -> new IllegalArgumentException(
            "해당번호로 가입한 사용자가 없습니다."));

        return user.getEmail();
    }

    public ResponseEntity<ResponseUserDto> profilePage(HttpServletRequest request, HttpServletResponse response) {
        //header 에서 accessToken 가져오기
        /*
        String authorizationHeader = request.getHeader("Authorization");
        String accessToken = null;
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            accessToken = authorizationHeader.substring(7);
        }
        
         */
        String accessToken = request.getHeader("access");
        //accessToken 없는경우
        if (accessToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Long id = jwtUtil.getId(accessToken);
            User savedUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("가입하지 않은 유저입니다."));
            return ResponseEntity.ok(new ResponseUserDto(savedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

    }

    @Transactional
    public ResponseEntity<String> updateProfile(Long id, RequestUserDto request) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        user.updateProfile(
            request.getName(),
            request.getPhone());
        return ResponseEntity.ok("저장되었습니다.");
    }
}
