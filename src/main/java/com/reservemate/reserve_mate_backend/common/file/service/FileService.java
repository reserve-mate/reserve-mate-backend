package com.reservemate.reserve_mate_backend.common.file.service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.file.dto.RequestImageUploadDto;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${spring.app.file.base-path}")
    private String basePath;

    @Value("${spring.app.file.profile-image}")
    private String profileImagePath;

    public FileService(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public ResponseEntity<?> uploadProfileImage(RequestImageUploadDto imageUploadDto,
        HttpServletRequest request) {
        //토큰에서 현재 로그인한 회원 아이디 꺼내기
        String accessToken = request.getHeader("access");
        //accessToken 없는경우
        if (accessToken == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Long id = jwtUtil.getId(accessToken);
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        MultipartFile file = imageUploadDto.getFile();

        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드한 파일이 비어있습니다.");
        }

        UUID uuid = UUID.randomUUID();
        String imageFileName = uuid + "_" + file.getOriginalFilename();
        //URL로 접근할 경로
        String savedUrl = "/" + profileImagePath + imageFileName;

        //디렉토리 경로
        String directoryPath = basePath + profileImagePath;
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }
        //실제 저장경로 (./uploads/profileImage/uuid_파일명)
        File destinationFile = new File(directory, imageFileName);

        try {
            file.transferTo(destinationFile);
            //기존 유저 이미지 url 업데이트
            user.updateProfileImage(savedUrl);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업데이트 중 오류 발생", e);
        }
        return ResponseEntity.ok().body("프로필 이미지가 성공적으로 업로드 되었습니다.");
    }

    /*
    * 파일 다중 업로드 : 파일 List + 폴더 저장위치
    * */
    public List<String> uploadFiles(List<MultipartFile> files, String targetFilePath){
        //디렉토리 경로
        String directoryPath = basePath + targetFilePath;
        File directory = new File(directoryPath);

        if (!directory.exists()){
            directory.mkdirs();
        }

        return files.stream()
            .filter(file -> !file.isEmpty())
            .map(file -> {
                String imageFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String savedUrl = "/" + targetFilePath + imageFileName;
                //실제 저장경로 (./uploads/targetFilePath/uuid_파일명)
                File destinationFile = new File(directory, imageFileName);

              try {
                file.transferTo(destinationFile);
                return savedUrl;
              } catch (IOException e) {
                throw new RuntimeException("파일 업로드 중 오류 발생", e);
              }
            })
            .toList();
    }
}
