package com.hckst.respal.jwt.handler;

import com.hckst.respal.common.exception.ErrorMessage;
import com.hckst.respal.domain.RefreshToken;
import com.hckst.respal.jwt.dto.Token;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component // 빈등록을 해줘야함
public class JwtTokenProvider {

    // 암호화키는 매우 중요하므로 따로 빼서 관리, application.yml의 @Value 사용 및 git 분리

    @Value("${jwt.secret-key.access-token}")
    private String accessSecretKey ;

    // 암호화키는 매우 중요하므로 따로 빼서 관리, application.yml의 @Value 사용 및 git 분리
    @Value("${jwt.secret-key.refresh-token}")
    private String refreshSecretKey ;


    // accessToken 기한
    private long accessTokenValidTime = 60 * 60 * 1000L; // 1시간
    // refreshToken 기한
    private long refreshTokenValidTime = 60 * 60 * 24 * 14 * 1000L; // 2주

    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        accessSecretKey = Base64.getEncoder().encodeToString(accessSecretKey.getBytes());
    }

    // 토큰에 저장할 유저 pk와 권한 리스트를 매개변수로 받아 access, refresh토큰을 생성하여 tokenDto 만들어 반환
    public Token createTokenWithRefresh(String membersLoginId, List<String> roles){
        Claims claims = Jwts.claims().setSubject(membersLoginId); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)  // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, refreshSecretKey)  // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .membersLoginId(membersLoginId)
                .build();
    }


    // JWT 토큰에서 시크릿 키로 검증 후 권한 목록을 가져옴
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옴. "Authorization" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // access 토큰의 유효성 + 만료일자 확인
    public boolean validateAccessToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (SignatureException e) {
            log.info("SignatureException");
            throw new JwtException(ErrorMessage.WRONG_TYPE_TOKEN.getMsg());
        } catch (MalformedJwtException e) {
            log.info("MalformedJwtException");
            throw new JwtException(ErrorMessage.UNSUPPORTED_TOKEN.getMsg());
        } catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException");
            throw new JwtException(ErrorMessage.EXPIRED_TOKEN.getMsg());
        } catch (IllegalArgumentException e) {
            log.info("IllegalArgumentException");
            throw new JwtException(ErrorMessage.UNKNOWN_ERROR.getMsg());
        }
    }

    // refresh 토큰의 유효성 + 만료일자 확인
    public String  validateRefreshToken(RefreshToken refreshTokenObj) {
        String refreshToken = refreshTokenObj.getRefreshToken();
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken);
            if (!claims.getBody().getExpiration().before(new Date())) {
                return recreationAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("roles"));
            }
        }catch (SignatureException e) {
            log.info("SignatureException");
            throw new JwtException(ErrorMessage.WRONG_TYPE_TOKEN.getMsg());
        } catch (MalformedJwtException e) {
            log.info("MalformedJwtException");
            throw new JwtException(ErrorMessage.UNSUPPORTED_TOKEN.getMsg());
        } catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException");
            throw new JwtException(ErrorMessage.EXPIRED_TOKEN.getMsg());
        } catch (IllegalArgumentException e) {
            log.info("IllegalArgumentException");
            throw new JwtException(ErrorMessage.UNKNOWN_ERROR.getMsg());
        }
        return null;
    }

    // refresh 토큰을 확인하여 access토큰을 재발급해줌
    public String recreationAccessToken(String userId, Object roles){

        Claims claims = Jwts.claims().setSubject(userId); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();

        //Access Token
        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, accessSecretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();

        return accessToken;
    }
}
