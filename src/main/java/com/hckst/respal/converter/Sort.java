package com.hckst.respal.converter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// Hub List 정렬조건
public enum Sort implements BaseEnumCode<String> {

    RECENT("recent"),
    VIEWS("views"),
    COMMENTS("comment");

    private final String value;
}
