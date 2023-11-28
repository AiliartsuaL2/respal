package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.RedirectType;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectCallBackResponse;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectResponse;
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
        RedirectType redirectType = RedirectType.findByValue(type);
        if(redirectType.equals(RedirectType.NULL)){
            throw new ApplicationException(ErrorMessage.INCORRECT_OAUTH_TYPE_EXCEPTION);
        }

        OauthTmp oauthTmp = oauthTmpRepository.findOauthTmpByUid(uid).orElseThrow(
                () -> new ApplicationException(ErrorMessage.NO_SUCH_OAUTH_TMP_UID_EXCEPTION));

        return getRedirectResponse(redirectType, oauthTmp);
    }

    private RedirectResponse getRedirectResponse(RedirectType redirectType, OauthTmp oauthTmp) {
        //todo npe 체크
        String provider = oauthTmp.getProvider().getValue();
        if(RedirectType.CALL_BACK.equals(redirectType) && oauthTmp.getRefreshToken() == null){
            throw new ApplicationException(ErrorMessage.INCORRECT_OAUTH_TYPE_EXCEPTION);
        }

        if(RedirectType.SIGN_UP.equals(redirectType) && oauthTmp.getRefreshToken() != null){
            throw new ApplicationException(ErrorMessage.INCORRECT_OAUTH_TYPE_EXCEPTION);
        }

        RedirectCallBackResponse redirectResponse = RedirectCallBackResponse.builder()
                .userInfo(oauthTmp.getUserInfo())
                .provider(provider)
                .accessToken(oauthTmp.getAccessToken())
                .refreshToken(oauthTmp.getRefreshToken())
                .build();
        return redirectResponse.of(redirectType);
    }
}
