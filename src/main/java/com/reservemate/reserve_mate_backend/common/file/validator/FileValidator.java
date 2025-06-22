package com.reservemate.reserve_mate_backend.common.file.validator;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;

public class FileValidator {

    private static final Map<String, String> ALLOWED_EXTENSIONS_AND_MIME_TYPES = Map.of(
        "jpg", "image/jpeg",
        "jpeg", "image/jpeg",
        "png", "image/png",
        "gif", "image/gif"
    );

    // 파일 검증
    public static void validatorFiles(List<MultipartFile> files) {
        for (MultipartFile multipartFile : files) {
            if (multipartFile.isEmpty())
                continue;   // 빈 파일은 건너 뜀

            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new ApiException(ErrorCode.NO_EXTENTION);
            }

            // 확장자 가져오기
            String extention = getExtension(originalFilename);
            String expectMimeType = ALLOWED_EXTENSIONS_AND_MIME_TYPES.get(extention);

            if (expectMimeType == null) {
                throw new ApiException(ErrorCode.INVALID_EXTENSION);
            }

            // MIME 타입 검사
            String actualMimeType = multipartFile.getContentType();
            if (actualMimeType == null || !actualMimeType.equalsIgnoreCase(expectMimeType)) {
                throw new ApiException(ErrorCode.INVALID_MIMETYPE);
            }
        }
    }

    private static String getExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }

}
