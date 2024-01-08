package com.hckst.respal.authentication.oauth.application;

import com.hckst.respal.authentication.jwt.dto.Token;
import com.hckst.respal.converter.Client;
import com.hckst.respal.converter.Provider;
import com.hckst.respal.members.presentation.dto.request.MembersJoinRequestDto;
import com.hckst.respal.members.presentation.dto.response.MembersLoginResponseDto;
import java.util.Optional;

public interface OAuthService2 {
    Token login(Provider provider, Client client, String code, String uid);
    MembersLoginResponseDto join(Provider provider, MembersJoinRequestDto membersJoinRequestDto);
    void logout(String refreshToken);
}
