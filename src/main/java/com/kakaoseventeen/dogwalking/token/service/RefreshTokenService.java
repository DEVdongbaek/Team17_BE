package com.kakaoseventeen.dogwalking.token.service;

import com.kakaoseventeen.dogwalking.token.domain.RefreshToken;
import com.kakaoseventeen.dogwalking.token.dto.RefreshResponseDTO;
import com.kakaoseventeen.dogwalking.token.repository.RefreshTokenJpaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.kakaoseventeen.dogwalking._core.security.JwtProvider.accessTokenValidTime;
import static com.kakaoseventeen.dogwalking._core.security.JwtProvider.createKey;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshResponseDTO refresh(String refreshToken){

        //클라이언트에게 받은 refresh token값 db에서 찾기
        RefreshToken refreshTokenOP = refreshTokenJpaRepository.findByToken(refreshToken).orElseThrow();
        String token = refreshTokenOP.getToken();

        try {
            //refresh token 검증을 한다.
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(createKey())
                    .build()
                    .parseClaimsJws(token);

            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성
            if (isNotExpired(claims)) {
                String accessToken = recreationAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("id"));
                return new RefreshResponseDTO(accessToken);
            }
        }catch (Exception e) {
            //JWT가 올바른 형식이 아닐 경우, JWT가 올바르게 구성되지 않았을 때 등등 -> 예외처리 필요
            return null;
        }
        //refresh 토큰이 만료되었을 경우, 로그인이 필요
        log.warn("토큰이 만료되었습니다");
        return null;
    }

    private static boolean isNotExpired(Jws<Claims> claims) {
        return !claims.getBody().getExpiration().before(new Date());
    }

    //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성
    public String recreationAccessToken(String email, Object id) {

        Claims claims = Jwts.claims().setSubject(String.valueOf(email));
        claims.put("id", id);

        String accessToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidTime))
                .setIssuedAt(new Date())
                .signWith(createKey(), SignatureAlgorithm.HS512)
                .compact();

        return accessToken;
    }

}
