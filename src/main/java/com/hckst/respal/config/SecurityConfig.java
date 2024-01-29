package com.hckst.respal.config;

import com.hckst.respal.authentication.jwt.application.TokenProvider;
import com.hckst.respal.authentication.jwt.handler.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션을 사용하지 않는다고 설정.
        http.httpBasic().disable()
                .authorizeRequests() // 요청에 대한 사용 권한 체크
                    .antMatchers("/test").authenticated() // authenticated : andMatchers의 URL로 요청이 오면 인증이 필요하다고 설정
                    .antMatchers(HttpMethod.POST,"/resume").authenticated()
                    .antMatchers(HttpMethod.DELETE,"/resume").authenticated()
                    .antMatchers(HttpMethod.GET,"/tagged").authenticated()
//                    .antMatchers("/admin/**").hasRole("ADMIN") // antMatchers : 해당 URL 요청시 설정해줌
//                    .antMatchers("/user/**").hasRole("USER")// hasRole : antPatterns URL로 요청이 들어오면 권한을 확인한다.
//                  .antMatchers(HttpMethod.POST,"/api/v1/board").authenticated() //  antPatterns 에 대한 HTTP POST 요청이 인증되어야 함을 말해 준다.
//                  .antMatchers("/api/v1/comment").authenticated()
                    .antMatchers("/**").permitAll()// permitAll : 다른 모든 요청들을 인증이나 권한 없이 허용
                    .and()
                .cors()
                    .and()
                .exceptionHandling()
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .and()
                // JwtAuthenticationFilter를 UserIdPasswordAuthenticationFilter 전에 넣는다 + 토큰에 저장된 유저정보를 활용하여야 하기 때문에 CustomUserDetailService 클래스를 생성
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), // 필터를 등록함, 파라미터 - 1번째 : 커스텀한 필터링, 2번쨰 : 필터링전 커스텀 필터링 수행
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter,JwtAuthenticationFilter.class); // jwt 에러처리를 위한 필터등록
    }
}
