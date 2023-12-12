package com.hckst.respal.converter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.hckst.respal.authentication.oauth.domain.RedirectType;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class ClientTest {
    private static final String DEV_SIGN_UP_URL = "https://localhost:3000/signup/social?uid=";
    private static final String DEV_CALL_BACK_URL = "https://localhost:3000/main";

    @Test
    void 웹_개발계_회원가입시_리다이렉트_URL_확인() {
        //given
        Client client = Client.WEB_DEV;
        RedirectType redirectType = RedirectType.SIGN_UP;
        String uid = UUID.randomUUID().toString();

        //when
        String uidRedirectUrl = client.getUidRedirectUrl(redirectType, uid);

        //then
        assertThat(uidRedirectUrl).contains(DEV_SIGN_UP_URL);
    }

    @Test
    void 웹_개발계_로그인시_리다이렉트_URL_확인() {
        //given
        Client client = Client.WEB_DEV;
        RedirectType redirectType = RedirectType.CALL_BACK;
        String uid = UUID.randomUUID().toString();

        //when
        String uidRedirectUrl = client.getUidRedirectUrl(redirectType, uid);

        //then
        assertThat(uidRedirectUrl).contains(DEV_CALL_BACK_URL);
    }
}
