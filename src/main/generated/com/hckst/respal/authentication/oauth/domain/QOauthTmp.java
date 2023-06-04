package com.hckst.respal.authentication.oauth.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOauthTmp is a Querydsl query type for OauthTmp
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOauthTmp extends EntityPathBase<OauthTmp> {

    private static final long serialVersionUID = 579865190L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOauthTmp oauthTmp = new QOauthTmp("oauthTmp");

    public final StringPath accessToken = createString("accessToken");

    public final StringPath endPoint = createString("endPoint");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.hckst.respal.converter.Provider> provider = createEnum("provider", com.hckst.respal.converter.Provider.class);

    public final StringPath refreshToken = createString("refreshToken");

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final com.hckst.respal.authentication.oauth.dto.request.info.QUserInfo userInfo;

    public QOauthTmp(String variable) {
        this(OauthTmp.class, forVariable(variable), INITS);
    }

    public QOauthTmp(Path<? extends OauthTmp> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOauthTmp(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOauthTmp(PathMetadata metadata, PathInits inits) {
        this(OauthTmp.class, metadata, inits);
    }

    public QOauthTmp(Class<? extends OauthTmp> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userInfo = inits.isInitialized("userInfo") ? new com.hckst.respal.authentication.oauth.dto.request.info.QUserInfo(forProperty("userInfo")) : null;
    }

}

