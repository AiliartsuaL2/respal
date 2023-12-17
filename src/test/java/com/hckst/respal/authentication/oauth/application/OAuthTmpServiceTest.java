package com.hckst.respal.authentication.oauth.application;
import com.hckst.respal.authentication.oauth.domain.OauthTmp;
import com.hckst.respal.authentication.oauth.domain.repository.OauthTmpRepository;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectCallBackResponse;
import com.hckst.respal.authentication.oauth.presentation.dto.response.RedirectResponse;
import com.hckst.respal.exception.ApplicationException;
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
        String uid = oauthTmp.getUid();
        String type = "callback";
        //when
        RedirectCallBackResponse response = (RedirectCallBackResponse) oAuthTmpService.getOauthTmp(uid, type);
        //then
        Assertions.assertThat(response.getRefreshToken()).isNotNull();
    }
    @Test
    void failGetCallbackOauthTmp(){
        //given
        OauthTmp oauthTmp = oauthTmpRepository.findById(SUCCESS_SIGNUP_OAUTH_TMP_ID).get();
        String uid = oauthTmp.getUid();
        String type = "callback";
        //when
        RedirectCallBackResponse response = (RedirectCallBackResponse) oAuthTmpService.getOauthTmp(uid, type);
        //then
        Assertions.assertThatExceptionOfType(ClassCastException.class);
    }
    @Test
    void successGetSignUpOauthTmp() {
        //given
        OauthTmp oauthTmp = oauthTmpRepository.findById(SUCCESS_SIGNUP_OAUTH_TMP_ID).get();
        String uid = oauthTmp.getUid();
        String type = "signup";
        //when
        RedirectResponse response = oAuthTmpService.getOauthTmp(uid, type);
        //then
        Assertions.assertThat(response.getUserInfo().getEmail()).isNotNull();
    }

    // uid가 존재하지 않는경우
    @Test
    void failGetSignUpOauthTmp(){
        //given
        String uid = "failUid";
        String type = "signup";
        //when
        //then
        Assertions.assertThatThrownBy(() -> oAuthTmpService.getOauthTmp(uid, type))
                .isInstanceOf(ApplicationException.class);
    }
}
