package com.hckst.respal.authentication.oauth.application;
import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.dto.response.RedirectCallBackResponse;
import com.hckst.respal.authentication.oauth.dto.response.RedirectResponse;
import com.hckst.respal.exception.oauth.NoSuchOAuthTmpEndpointException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OAuthTmpServiceTest {

    final static Long SUCCESS_CALLBACK_OAUTH_TMP_ID = 2L;
    final static Long SUCCESS_SIGNUP_OAUTH_TMP_ID = 1L;
    final static Long FAIL_OAUTH_TMP_ID = 5L;
    @Autowired
    OAuthTmpService oAuthTmpService;
    @Autowired
    OauthTmpRepository oauthTmpRepository;
    @Test
    void successGetCallbackOauthTmp() {
        //given
        OauthTmp oauthTmp = oauthTmpRepository.findById(SUCCESS_CALLBACK_OAUTH_TMP_ID).get();
        String endpoint = oauthTmp.getEndpoint();
        String type = "callback";
        //when
        RedirectCallBackResponse response = (RedirectCallBackResponse) oAuthTmpService.getOauthTmp(endpoint, type);
        //then
        Assertions.assertThat(response.getRefreshToken()).isNotNull();
    }
    @Test
    void failGetCallbackOauthTmp(){
        //given
        OauthTmp oauthTmp = oauthTmpRepository.findById(SUCCESS_SIGNUP_OAUTH_TMP_ID).get();
        String endpoint = oauthTmp.getEndpoint();
        String type = "callback";
        //when
        RedirectCallBackResponse response = (RedirectCallBackResponse) oAuthTmpService.getOauthTmp(endpoint, type);
        //then
        Assertions.assertThatExceptionOfType(ClassCastException.class);
    }
    @Test
    void successGetSignUpOauthTmp() {
        //given
        OauthTmp oauthTmp = oauthTmpRepository.findById(SUCCESS_SIGNUP_OAUTH_TMP_ID).get();
        String endpoint = oauthTmp.getEndpoint();
        String type = "signup";
        //when
        RedirectResponse response = oAuthTmpService.getOauthTmp(endpoint, type);
        //then
        Assertions.assertThat(response.getUserInfo().getEmail()).isNotNull();
    }

    // endpoint가 존재하지 않는경우
    @Test
    void failGetSignUpOauthTmp(){
        //given
        String endpoint = "failEndpoint";
        String type = "signup";
        //when
        //then
        Assertions.assertThatThrownBy(() -> oAuthTmpService.getOauthTmp(endpoint, type))
                .isInstanceOf(NoSuchOAuthTmpEndpointException.class);
    }
}