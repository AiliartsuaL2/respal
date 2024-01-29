package com.hckst.respal.authentication.jwt.domain;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.exception.ErrorMessage;
import io.jsonwebtoken.JwtException;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "REFRESH_TOKEN", nullable = false)
    private String refreshToken;
    @Column(name = "MEMBER_ID", nullable = false)
    private Long keyId;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public RefreshToken (String refreshToken, Long keyId) {
        this.refreshToken = refreshToken;
        this.keyId = keyId;
    }

    public void validateSameRefreshToken(String refreshToken) {
        if (!this.refreshToken.equals(refreshToken)) {
            throw new JwtException(ErrorMessage.INCORRECT_REFRESH_TOKEN_EXCEPTION.getMsg());
        }
    }
}
