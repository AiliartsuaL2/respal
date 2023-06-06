package com.hckst.respal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        config.addAllowedOrigin("http://localhost:3000");           // 로컬
//        config.addAllowedOrigin("http://respal.me");          // 개발

        config.setAllowedMethods(Arrays.asList("POST", "OPTIONS", "GET", "DELETE", "PUT", "PATCH"));
        config.setAllowedHeaders(
                Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization",
                        "x-nz-mid"));
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}