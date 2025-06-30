package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MatchDetailDto {

    private MatchDataDto matchDataDto;
    private FacilityDataDto facilityDataDto;
    private int playerCnt;                  // 준비된 참가인원 수
    private List<MatchPlayerDto> playerDtos;// 참가자 목록록
    private UserDataDto userDataDto;

    public static MatchDetailDto toMatchDetailDto(Match match, User user,
        List<MatchPlayer> matchPlayers, List<FacilityImage> images, Payment payment, String fileBasepath) {

        MatchDetailDto detailDto = null;

        if (user == null) {
            detailDto = MatchDetailDto.builder()
                .matchDataDto(MatchDataDto.toMatchDataDto(match))
                .facilityDataDto(FacilityDataDto.toFacilityDataDto(match, images, fileBasepath))
                .playerCnt(matchPlayers.size())
                .playerDtos(MatchPlayerDto.toMatchPlayerDtos(matchPlayers))
                .build();
        } else {
            detailDto = MatchDetailDto.builder()
                .matchDataDto(MatchDataDto.toMatchDataDto(match))
                .facilityDataDto(FacilityDataDto.toFacilityDataDto(match, images, fileBasepath))
                .playerCnt(matchPlayers.size())
                .playerDtos(MatchPlayerDto.toMatchPlayerDtos(matchPlayers))
                .userDataDto(UserDataDto.toUserDataDto(user, matchPlayers, payment))
                .build();
        }

        return detailDto;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class MatchDataDto {

        private Long matchId;                   // 매치 일련번호
        private String matchName;              // 매치 제목
        private String manager;                 // 매치 매니저
        private String mangerImage;             // 매니저 이미지
        private MatchStatus matchStatus;        // 매치 상태
        private int teamCapacity;               // 최대 인원 수
        private String description;             // 매치 설명
        private String matchDate;               // 매치 날짜
        private int matchTime;                  // 매치 시작시간
        private int matchEndTime;               // 매치 종료시간
        private int matchPrice;                 // 매치 가격

        public static MatchDataDto toMatchDataDto(Match match) {
            MatchDataDto matchDataDto = MatchDataDto.builder()
                .matchId(match.getMatchId())
                .matchName(match.getMatchName())
                .manager(match.getFacilityManager().getUser().getName())
                .mangerImage(match.getFacilityManager().getUser().getProfileImage())
                .matchStatus(match.getMatchStatus())
                .teamCapacity(match.getTeamCapacity())
                .description(match.getDescription())
                .matchDate(Utils.localDateFormatWeek(match.getMatchDate()))
                .matchTime(match.getMatchTime())
                .matchEndTime(match.getEndTime())
                .matchPrice(match.getMatchPrice())
                .build();

            return matchDataDto;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class FacilityDataDto {

        private String facilityName;            // 시설명
        private String address;                 // 주소
        private String courtName;               // 코트명
        private SportType sportType;            // 종목
        private List<String> imageDtos;         // 시설 이미지 목록

        public static FacilityDataDto toFacilityDataDto(Match match, List<FacilityImage> images, String fileBasepath) {

            Address address = match.getCourt().getFacility().getAddress();

            FacilityDataDto facilityDto = FacilityDataDto.builder()
                .facilityName(match.getCourt().getFacility().getName())
                .address(address.getFullAddress())
                .courtName(match.getCourt().getName())
                .sportType(match.getCourt().getFacility().getSportType())
                .imageDtos(getFacilityImages(images, fileBasepath))
                .build();

            return facilityDto;
        }

        private static List<String> getFacilityImages(List<FacilityImage> images, String fileBasepath) {
            List<String> facilityImages = images.stream()
                .map(image -> fileBasepath + image.getImageUrl())
                .toList();

            return facilityImages;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class UserDataDto {

        private String userName;
        private String phone;
        private String userEmail;
        private String orderId;

        @JsonProperty("isMatchApply")
        private boolean isMatchApply;

        public static UserDataDto toUserDataDto(User user, List<MatchPlayer> matchPlayers, Payment payment) {

            UserDataDto userDto = UserDataDto.builder()
                .userName(user.getName())
                .phone(user.getPhone())
                .userEmail(user.getEmail())
                .isMatchApply(false)
                .build();

            if (payment != null) {
                userDto = UserDataDto.builder()
                    .userName(user.getName())
                    .phone(user.getPhone())
                    .userEmail(user.getEmail())
                    .isMatchApply(true)
                    .orderId(payment.getImpUid())
                    .build();
            }

            return userDto;
        }

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    static class MatchPlayerDto {

        private Long playerId;
        private String userName;
        private String profileImage;

        public static List<MatchPlayerDto> toMatchPlayerDtos(List<MatchPlayer> matchPlayers) {

            List<MatchPlayerDto> playerDtos = matchPlayers.stream()
                .map(MatchPlayerDto::toMatchPlayerDto).toList();

            return playerDtos;
        }

        private static MatchPlayerDto toMatchPlayerDto(MatchPlayer matchPlayer) {
            return MatchPlayerDto.builder()
                .playerId(matchPlayer.getPlayerId())
                .userName(matchPlayer.getUser().getName())
                .profileImage(matchPlayer.getUser().getProfileImage())
                .build();
        }
    }

}
