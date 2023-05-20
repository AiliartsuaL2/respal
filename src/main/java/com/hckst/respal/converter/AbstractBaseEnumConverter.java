package com.hckst.respal.converter;

import javax.persistence.AttributeConverter;
import java.util.Arrays;

// BaseEnumCode<T> 를 상속받는 Enum 에 대한 Converter이기 때문에 <T> 타입에 대한 values를 알 수 없어 abstract 클래스로 정의
public abstract class AbstractBaseEnumConverter<X extends Enum<X> & BaseEnumCode<Y>, Y> implements AttributeConverter<X, Y> {

    protected abstract X[] getValueList();

    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return attribute.getValue();
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        return Arrays.stream(getValueList())
                .filter(e -> e.getValue().equals(dbData))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type for %s.", dbData)));
    }
}

