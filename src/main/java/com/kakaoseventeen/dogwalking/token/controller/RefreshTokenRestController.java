package com.kakaoseventeen.dogwalking.token.controller;

import com.kakaoseventeen.dogwalking._core.utils.ApiUtils;
import com.kakaoseventeen.dogwalking.member.service.MemberService;
import com.kakaoseventeen.dogwalking.token.dto.RefreshResponseDTO;
import com.kakaoseventeen.dogwalking.token.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RefreshTokenRestController {
    private final RefreshTokenService refreshTokenService;

    //access token 만료시간 지나면 프론트가 refresh token을 헤더에 담아 요청 보내줌
    @GetMapping("/refresh")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization-refresh") String refreshToken){
        RefreshResponseDTO responseDTO = refreshTokenService.refresh(refreshToken);
        //새로운 accesstoken을 반환
        ApiUtils.ApiResult<?> apiResult = ApiUtils.success(responseDTO);
        return ResponseEntity.ok(apiResult);
    }
}
