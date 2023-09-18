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

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMembers members = new QMembers("members");

    public final ListPath<com.hckst.respal.comment.domain.Comment, com.hckst.respal.comment.domain.QComment> commentList = this.<com.hckst.respal.comment.domain.Comment, com.hckst.respal.comment.domain.QComment>createList("commentList", com.hckst.respal.comment.domain.Comment.class, com.hckst.respal.comment.domain.QComment.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QJob job;

    public final StringPath nickname = createString("nickname");

    public final ListPath<com.hckst.respal.authentication.oauth.domain.Oauth, com.hckst.respal.authentication.oauth.domain.QOauth> oauthList = this.<com.hckst.respal.authentication.oauth.domain.Oauth, com.hckst.respal.authentication.oauth.domain.QOauth>createList("oauthList", com.hckst.respal.authentication.oauth.domain.Oauth.class, com.hckst.respal.authentication.oauth.domain.QOauth.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final EnumPath<com.hckst.respal.converter.TFCode> passwordTmpYn = createEnum("passwordTmpYn", com.hckst.respal.converter.TFCode.class);

    public final StringPath picture = createString("picture");

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final ListPath<com.hckst.respal.resume.domain.Resume, com.hckst.respal.resume.domain.QResume> resumeList = this.<com.hckst.respal.resume.domain.Resume, com.hckst.respal.resume.domain.QResume>createList("resumeList", com.hckst.respal.resume.domain.Resume.class, com.hckst.respal.resume.domain.QResume.class, PathInits.DIRECT2);

    public final EnumPath<RoleType> roleType = createEnum("roleType", RoleType.class);

    public final ListPath<com.hckst.respal.tag.domain.Tag, com.hckst.respal.tag.domain.QTag> taggedList = this.<com.hckst.respal.tag.domain.Tag, com.hckst.respal.tag.domain.QTag>createList("taggedList", com.hckst.respal.tag.domain.Tag.class, com.hckst.respal.tag.domain.QTag.class, PathInits.DIRECT2);

    public QMembers(String variable) {
        this(Members.class, forVariable(variable), INITS);
    }

    public QMembers(Path<? extends Members> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMembers(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMembers(PathMetadata metadata, PathInits inits) {
        this(Members.class, metadata, inits);
    }

    public QMembers(Class<? extends Members> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.job = inits.isInitialized("job") ? new QJob(forProperty("job")) : null;
    }

}

