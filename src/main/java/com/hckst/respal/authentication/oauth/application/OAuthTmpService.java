package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.dto.response.RedirectCallBackResponse;
import com.hckst.respal.authentication.oauth.dto.response.RedirectResponse;
import com.hckst.respal.converter.ProviderConverter;
import com.hckst.respal.exception.oauth.NoSuchOAuthTmpEndpointException;
import com.hckst.respal.exception.oauth.WrongTypeSettionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthTmpService {
    private final OauthTmpRepository oauthTmpRepository;

    public RedirectResponse getOauthTmp(String endpoint, String type) {
        if(!(type.equals("callback") || type.equals("signup"))){
            throw new WrongTypeSettionException();
        }
        OauthTmp oauthTmp = oauthTmpRepository.findOauthTmpByEndpoint(endpoint).orElseThrow(
                () -> new NoSuchOAuthTmpEndpointException());
        ProviderConverter pc = new ProviderConverter();
        String provider = pc.convertToDatabaseColumn(oauthTmp.getProvider());
        if("callback".equals(type)){
            if(oauthTmp.getRefreshToken() == null){
                throw new WrongTypeSettionException();
            }
            return RedirectCallBackResponse.builder()
                    .userInfo(oauthTmp.getUserInfo())
                    .provider(provider)
                    .accessToken(oauthTmp.getAccessToken())
                    .refreshToken(oauthTmp.getRefreshToken())
                    .build();
        }else if("signup".equals(type)){
            if(oauthTmp.getRefreshToken() != null){
                throw new WrongTypeSettionException();
            }
            return RedirectResponse.builder()
                    .userInfo(oauthTmp.getUserInfo())
                    .provider(provider)
                    .build();
        }
        return null;
    }
}
