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

    private Long matchId;

    private String facilityName;
    private String address;
    private String courtName;
    private SportType sportType;

    private String manager;
    private MatchStatus matchStatus;
    private int teamCapacity;
    private String description;
    private String matchDate;
    private int matchTime;
    private int matchPrice;
    private int playerCnt;
    private String phone;
    private String userName;
    private String profileImage;
    private List<MatchPlayerDto> playerDtos;
    private List<String> imageDtos;

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
            .matchPrice(match.getMatchPrice())
            .phone(user.getPhone())
            .userName(user.getName())
            .profileImage(user.getProfileImage())
            .playerCnt(matchPlayers.size())
            .playerDtos(MatchPlayerDto.toMatchPlayerDtos(matchPlayers))
            .imageDtos(getFacilityImages(images))
            .build();
        return detailDto;
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
