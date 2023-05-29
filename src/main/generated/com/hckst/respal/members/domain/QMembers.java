package com.hckst.respal.members.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMembers is a Querydsl query type for Members
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMembers extends EntityPathBase<Members> {

    private static final long serialVersionUID = -1087310675L;

    public static final QMembers members = new QMembers("members");

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath nickname = createString("nickname");

    public final ListPath<com.hckst.respal.authentication.oauth.domain.Oauth, com.hckst.respal.authentication.oauth.domain.QOauth> oauthList = this.<com.hckst.respal.authentication.oauth.domain.Oauth, com.hckst.respal.authentication.oauth.domain.QOauth>createList("oauthList", com.hckst.respal.authentication.oauth.domain.Oauth.class, com.hckst.respal.authentication.oauth.domain.QOauth.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final StringPath picture = createString("picture");

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final ListPath<Role, QRole> roles = this.<Role, QRole>createList("roles", Role.class, QRole.class, PathInits.DIRECT2);

    public QMembers(String variable) {
        super(Members.class, forVariable(variable));
    }

    public QMembers(Path<? extends Members> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMembers(PathMetadata metadata) {
        super(Members.class, metadata);
    }

}

