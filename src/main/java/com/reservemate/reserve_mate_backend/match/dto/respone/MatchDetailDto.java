package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.util.List;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
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

    private Long matchId;                   // 매치 일련번호

    private String facilityName;            // 시설명
    private String address;                 // 주소
    private String courtName;               // 코트명
    private SportType sportType;            // 종목

    private String manager;                 // 매치 매니저
    private MatchStatus matchStatus;        // 매치 상태
    private int teamCapacity;               // 최대 인원 수
    private String description;             // 매치 설명
    private String matchDate;               // 매치 날짜
    private int matchTime;                  // 매치 시작시간
    private int matchEndTime;               // 매치 종료시간
    private int matchPrice;                 // 매치 가격
    private int playerCnt;                  // 준비된 참가인원 수

    private String phone;                   // 본인 전화번호
    private String userName;                // 본인 이름
    private List<MatchPlayerDto> playerDtos;// 참가자 목록록
    private List<String> imageDtos;         // 시설 이미지 목록

    private boolean isMatchApply;

    public static MatchDetailDto toMatchDetailDto(Match match, User user,
        List<MatchPlayer> matchPlayers, List<FacilityImage> images) {

        Address address = match.getCourt().getFacility().getAddress();

        MatchDetailDto detailDto = MatchDetailDto.builder()
            .matchId(match.getMatchId())
            .facilityName(match.getCourt().getFacility().getName())
            .address(address.getFullAddress())
            .courtName(match.getCourt().getName())
            .sportType(match.getCourt().getFacility().getSportType())
            .manager(match.getFacilityManager().getUser().getName())
            .matchStatus(match.getMatchStatus())
            .teamCapacity(match.getTeamCapacity())
            .description(match.getDescription())
            .matchDate(Utils.localDateFormatWeek(match.getMatchDate()))
            .matchTime(match.getMatchTime())
            .matchEndTime(match.getEndTime())
            .matchPrice(match.getMatchPrice())
            .phone(user.getPhone())
            .userName(user.getName())
            .playerCnt(matchPlayers.size())
            .playerDtos(MatchPlayerDto.toMatchPlayerDtos(matchPlayers))
            .imageDtos(getFacilityImages(images))
            .isMatchApply(isMatchApply(matchPlayers, user.getId()))
            .build();
        return detailDto;
    }

    private static boolean isMatchApply(List<MatchPlayer> matchPlayers, Long userId) {

        boolean isMatch = false;

        for (MatchPlayer matchPlayer : matchPlayers) {
            if (matchPlayer.getUser().getId() == userId) {
                isMatch = true;
                break;
            }
        }

        return isMatch;
    }

    private static List<String> getFacilityImages(List<FacilityImage> images) {
        List<String> facilityImages = images.stream()
            .map(image -> image.getImageUrl()).toList();
        return facilityImages;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @Setter
    static class MatchPlayerDto {

        private Long playerId;
        private String userName;

        public static List<MatchPlayerDto> toMatchPlayerDtos(List<MatchPlayer> matchPlayers) {

            List<MatchPlayerDto> playerDtos = matchPlayers.stream()
                .map(MatchPlayerDto::toMatchPlayerDto).toList();

            return playerDtos;
        }

        private static MatchPlayerDto toMatchPlayerDto(MatchPlayer matchPlayer) {
            return MatchPlayerDto.builder()
                .playerId(matchPlayer.getPlayerId())
                .userName(matchPlayer.getUser().getName())
                .build();
        }
    }

}
