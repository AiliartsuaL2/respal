package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectCallBackResponse;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectResponse;
import com.hckst.respal.converter.ProviderConverter;
import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthTmpService {
    private final OauthTmpRepository oauthTmpRepository;

    public RedirectResponse getOauthTmp(String uid, String type) {
        if(!(type.equals("callback") || type.equals("signup"))){
            throw new ApplicationException(ErrorMessage.INCORRECT_OAUTH_TYPE_EXCEPTION);
        }
        OauthTmp oauthTmp = oauthTmpRepository.findOauthTmpByUid(uid).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NO_SUCH_OAUTH_TMP_UID_EXCEPTION));
        ProviderConverter pc = new ProviderConverter();
        String provider = pc.convertToDatabaseColumn(oauthTmp.getProvider());
        if("callback".equals(type)){
            if(oauthTmp.getRefreshToken() == null){
                throw new ApplicationException(ErrorMessage.INCORRECT_OAUTH_TYPE_EXCEPTION);
            }
            return RedirectCallBackResponse.builder()
                    .userInfo(oauthTmp.getUserInfo())
                    .provider(provider)
                    .accessToken(oauthTmp.getAccessToken())
                    .refreshToken(oauthTmp.getRefreshToken())
                    .build();
        }else if("signup".equals(type)){
            if(oauthTmp.getRefreshToken() != null){
                throw new ApplicationException(ErrorMessage.INCORRECT_OAUTH_TYPE_EXCEPTION);
            }
            return RedirectResponse.builder()
                    .userInfo(oauthTmp.getUserInfo())
                    .provider(provider)
                    .build();
        }
        return null;
    }
}
