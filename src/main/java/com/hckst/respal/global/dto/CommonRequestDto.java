package com.hckst.respal.global.dto;

import com.hckst.respal.exception.ApplicationException;
import com.hckst.respal.exception.ErrorMessage;
import org.apache.commons.lang3.ObjectUtils;

public abstract class CommonRequestDto {
    public abstract void checkRequiredFieldIsNull();
    protected <T> void checkNull (T field, ErrorMessage errorMessage) {
        if(ObjectUtils.isEmpty(field)) {
            throw new IllegalArgumentException(errorMessage.getMsg());
        }
    }
}
