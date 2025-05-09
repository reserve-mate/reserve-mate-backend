package com.reservemate.reserve_mate_backend.admin.facilities.service;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateFacility;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestFacilityImageUploadDto;
import com.reservemate.reserve_mate_backend.common.file.service.FileService;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AdminFacilityService {

    private final FacilityRepository facilityRepository;
    private final CourtRepository courtRepository;
    private final OperationHourRepository operationHourRepository;
    private final FileService fileService;
    private final FacilityImageRepository facilityImageRepository;

    @Value("${spring.app.file.facility-images}")
    private String facilityImagesPath;

    public AdminFacilityService(FacilityRepository facilityRepository,
        CourtRepository courtRepository, OperationHourRepository operationHourRepository,
        FileService fileService, FacilityImageRepository facilityImageRepository) {
        this.facilityRepository = facilityRepository;
        this.courtRepository = courtRepository;
        this.operationHourRepository = operationHourRepository;
        this.fileService = fileService;
        this.facilityImageRepository = facilityImageRepository;
    }

    @Transactional
    public void createFacility(RequestCreateFacility requestCreateFacility, List<MultipartFile> images,
        List<RequestFacilityImageUploadDto> facilityImageUploadDtoList) {
        //setting facility data
        Facility facility = Facility.create(requestCreateFacility);
        Facility savedFacility = facilityRepository.save(facility);

        //setting operatingHour
        List<OperatingHour> hours = requestCreateFacility.getOperatingHours().stream()
            .map(hourDto -> OperatingHour.create(hourDto, savedFacility))
            .toList();

        operationHourRepository.saveAll(hours);

        //setting court
        List<Court> courts = requestCreateFacility.getCourts().stream()
            .map(courtDto -> Court.create(courtDto, savedFacility))
            .toList();

        courtRepository.saveAll(courts);

        //이미지 저장
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

    public ResponseEntity<Slice<FacilityDto>> getAdminFacilityList(String keyword, long lastId, Pageable pageable) {
        RequestFacilitySearchDto facilitySearchDto = RequestFacilitySearchDto.builder()
            .keyword(keyword)
            .lastId(lastId == 0 ? null : lastId)
            .build();

        Slice<FacilityDto> facilityList = facilityRepository.findAllByCursor(facilitySearchDto, pageable);
        return ResponseEntity.ok(facilityList);
    }

}
