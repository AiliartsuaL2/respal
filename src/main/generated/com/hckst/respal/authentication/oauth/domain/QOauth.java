package com.hckst.respal.authentication.oauth.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOauth is a Querydsl query type for Oauth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOauth extends EntityPathBase<Oauth> {

    private static final long serialVersionUID = 814435569L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOauth oauth = new QOauth("oauth");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.hckst.respal.members.domain.QMembers membersId;

    public final EnumPath<com.hckst.respal.converter.Provider> provider = createEnum("provider", com.hckst.respal.converter.Provider.class);

    public QOauth(String variable) {
        this(Oauth.class, forVariable(variable), INITS);
    }

    public QOauth(Path<? extends Oauth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOauth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOauth(PathMetadata metadata, PathInits inits) {
        this(Oauth.class, metadata, inits);
    }

    public QOauth(Class<? extends Oauth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.membersId = inits.isInitialized("membersId") ? new com.hckst.respal.members.domain.QMembers(forProperty("membersId"), inits.get("membersId")) : null;
    }

}

