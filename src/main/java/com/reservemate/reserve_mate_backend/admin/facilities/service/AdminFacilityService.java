package com.reservemate.reserve_mate_backend.admin.facilities.service;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestAssignManagersDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateCourtDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateFacilityDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestFacilityImageUploadDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.response.ResponseAdminFacilityDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.response.ResponseFacilityManagerDto;
import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.file.service.FileService;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.ManagerRole;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityNameResponseDto;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AdminFacilityService {

    private final FacilityManagerRepository facilityManagerRepository;
    private final FacilityRepository facilityRepository;
    private final CourtRepository courtRepository;
    private final OperationHourRepository operationHourRepository;
    private final FileService fileService;
    private final FacilityImageRepository facilityImageRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.app.file.facility-images}")
    private String facilityImagesPath;

    private final UserRepository userRepository;

    /* 대시보드 시설 목록 */
    public List<FacilityDto> getDashboardFacilities(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        List<FacilityManager> facilities = facilityManagerRepository.findByUser(user);
        List<Long> facilityIds  = FacilityManager.getFacilityIds(facilities);

        List<FacilityDto> response = facilityRepository.findDashboardFacilities(facilityIds);

        return response;
    }

    // 매치 등록 시 시설명 조회
    public List<FacilityNameResponseDto> getMatchFacilityNames(HttpServletRequest request, SportType sportType) {

        String accessToken = request.getHeader("access");
        if (accessToken == null) {
            throw new ApiException(ErrorCode.ADMIN_FORBIDDEN);
        }

        Long userId = jwtUtil.getId(accessToken);

        List<FacilityManager> facilityManagers = facilityManagerRepository.findByUserId(userId);

        List<OperatingHour> hours = operationHourRepository.findByFacilityInAndDayOfWeek(
            FacilityManager.getFacilityIds(facilityManagers), Utils.getDayOfWeek());

        return FacilityNameResponseDto.getFacilities(facilityManagers, sportType, hours);
    }

    @Transactional
    public void createFacility(RequestCreateFacilityDto requestCreateFacilityDto, List<MultipartFile> images,
        List<RequestFacilityImageUploadDto> facilityImageUploadDtoList, CustomUserDetails customUserDetails) {
        //setting facility data
        Facility facility = Facility.create(requestCreateFacilityDto);
        Facility savedFacility = facilityRepository.save(facility);

        //setting operatingHour
        List<OperatingHour> hours = requestCreateFacilityDto.getOperatingHours().stream()
            .map(hourDto -> OperatingHour.create(hourDto, savedFacility))
            .toList();

        operationHourRepository.saveAll(hours);

        //setting court
        List<Court> courts = requestCreateFacilityDto.getCourts().stream()
            .map(courtDto -> Court.create(courtDto, savedFacility))
            .toList();

        courtRepository.saveAll(courts);

        //이미지 저장
        if (images == null || images.isEmpty()) {
            return;
        }
        if (facilityImageUploadDtoList == null || images.size() != facilityImageUploadDtoList.size()) {
            throw new ApiException(ErrorCode.IMAGE_METADATA_MISMATCH);
        }
        if (images != null && !images.isEmpty() && images.size() == facilityImageUploadDtoList.size()) {
            List<String> imagePaths = fileService.uploadFiles(images, facilityImagesPath);
            // 이미지 저장 리스트 + 받아온 이미지 세부정보 리스트
            List<FacilityImage> image = IntStream.range(0, imagePaths.size())
                .mapToObj(i -> {
                    String path = imagePaths.get(i);
                    RequestFacilityImageUploadDto meta = facilityImageUploadDtoList.get(i);
                    return FacilityImage.create(path, meta.isMain(), meta.getDisplayOrder(), savedFacility);
                })
                .toList();

            facilityImageRepository.saveAll(image);
        }

        //시설 생성자 owner 로 시설 관리자 등록
        User user = userRepository.findById(customUserDetails.getId())
            .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));
        FacilityManager facilityManager = FacilityManager.create(
            savedFacility,
            user,
            ManagerRole.OWNER);

        facilityManagerRepository.save(facilityManager);

    }

    public ResponseEntity<Slice<FacilityDto>> getAdminFacilityList(String keyword, long lastId, Pageable pageable) {
        RequestFacilitySearchDto facilitySearchDto = RequestFacilitySearchDto.builder()
            .keyword(keyword)
            .lastId(lastId == 0 ? null : lastId)
            .size(pageable.getPageSize())
            .build();

        Slice<FacilityDto> facilityList = facilityRepository.findAllByCursor(facilitySearchDto, pageable);
        return ResponseEntity.ok(facilityList);
    }

    public ResponseAdminFacilityDto detailAdminFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));
        List<OperatingHour> operatingHours = operationHourRepository.findByFacility(facility);
        List<Court> courts = courtRepository.findByFacility(facility);
        return ResponseAdminFacilityDto.getFacility(facility, operatingHours, courts);
    }

    @Transactional
    public void updateFacility(Long id, RequestCreateFacilityDto requestUpdateFacilityDto) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));

        String conventient = Facility.setConventient(
            requestUpdateFacilityDto.isHasParking(),
            requestUpdateFacilityDto.isHasShower(),
            requestUpdateFacilityDto.isHasEquipmentRental(),
            requestUpdateFacilityDto.isHasCafe()
        );
        //update facility
        facility.update(
            requestUpdateFacilityDto.getName(),
            requestUpdateFacilityDto.getDescription(),
            requestUpdateFacilityDto.getAddress(),
            conventient
        );

        //delete operatingHour
        operationHourRepository.softDeleteByFacility(facility);

        //insert new operatingHour
        List<OperatingHour> newOperatingHours = requestUpdateFacilityDto.getOperatingHours().stream()
            .map(dto -> OperatingHour.create(dto, facility))
            .toList();

        operationHourRepository.saveAll(newOperatingHours);
    }

    @Transactional
    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));

        // 연관 테이블 소프트 삭제
        operationHourRepository.softDeleteByFacility(facility);
        courtRepository.softDeleteByFacility(facility);
        facilityImageRepository.deleteByFacility(facility);

        //시설 삭제
        facilityRepository.delete(facility);
    }

    @Transactional
    public void createCourt(Long facilityId, RequestCreateCourtDto createCourt) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));
        courtRepository.save(Court.create(createCourt, facility));
    }

    @Transactional
    public void updateCourt(Long facilityId, Long courtId, RequestCreateCourtDto requestUpdateCourtDto) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));
        Court court = courtRepository.findByIdAndFacility(courtId, facility)
            .orElseThrow(() -> new EntityNotFoundException("해당 코트가 존재하지 않습니다."));
        court.update(
            requestUpdateCourtDto.getName(),
            requestUpdateCourtDto.getCourtType(),
            requestUpdateCourtDto.getWidth(),
            requestUpdateCourtDto.getHeight(),
            requestUpdateCourtDto.getIndoor(),
            requestUpdateCourtDto.getActive(),
            requestUpdateCourtDto.getFee()
        );
    }

    @Transactional
    public void deleteCourt(Long facilityId, Long courtId) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));
        Court court = courtRepository.findByIdAndFacility(courtId, facility)
            .orElseThrow(() -> new EntityNotFoundException("해당 코트가 존재하지 않습니다."));
        court.delete();
    }

    @Transactional
    public void assignManager(Long id, RequestAssignManagersDto assignManagersDto) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));

        User user = userRepository.findByEmailAndName(assignManagersDto.getEmail(), assignManagersDto.getUserName())
            .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        boolean isRegistered = facilityManagerRepository.existsByUser_IdAndFacility_Id(user.getId(), id);

        //시설에 이미 등록되어 있는경우
        if (isRegistered) {
            throw new ApiException(ErrorCode.FACILITY_MANAGER_ALREADY_REGISTERD);   //이미 매니저로 등록되어 있는 회원입니다.
        }
        //유저 롤이 관리자가 아닌 경우
        if (!user.getRole().equals(UserRole.ROLE_ADMIN)) {
            long facilityManagerCount = facilityManagerRepository.countByUser_Id(user.getId());
            if (facilityManagerCount >= 1) {
                throw new ApiException(ErrorCode.FACILITY_MANAGER_ALREADY_ASSIGED_TO_ANOTHER_FACILITY); //시설 매니저는 1개의 시설만 관리 할 수 있습니다.
            }
        }

        FacilityManager facilityManager = FacilityManager.create(facility, user, assignManagersDto);
        facilityManagerRepository.save(facilityManager);

        if (user.getRole() == UserRole.ROLE_USER) {
            user.updateRole(UserRole.ROLE_FACILITY_MANAGER);
        }

    }

    public List<ResponseFacilityManagerDto> getFacilityManagerList(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설이 존재하지 않습니다."));
        List<FacilityManager> facilityManagers = facilityManagerRepository.findByFacility(facility);
        return facilityManagers.stream()
            .map(ResponseFacilityManagerDto::convertFacilityManager)
            .collect(Collectors.toList());
    }

    @Transactional
    public void removeFacilityManager(Long facilityId, Long id) {
        FacilityManager facilityManager = facilityManagerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("해당 시설 관리자가 존재하지 않습니다."));

        if (!facilityManager.getFacility().getId().equals(facilityId)) {
            throw new IllegalArgumentException("시설 ID와 관리자 정보가 일치하지 않습니다.");
        }
        facilityManagerRepository.delete(facilityManager);
    }
}
