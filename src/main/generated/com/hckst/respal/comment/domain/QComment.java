package com.hckst.respal.comment.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = 1900347373L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> deleteTime = createDateTime("deleteTime", java.time.LocalDateTime.class);

    public final EnumPath<com.hckst.respal.converter.TFCode> deleteYn = createEnum("deleteYn", com.hckst.respal.converter.TFCode.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.hckst.respal.members.domain.QMembers members;

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final com.hckst.respal.resume.domain.QResume resume;

    public final NumberPath<Integer> xLocation = createNumber("xLocation", Integer.class);

    public final NumberPath<Integer> yLocation = createNumber("yLocation", Integer.class);

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.members = inits.isInitialized("members") ? new com.hckst.respal.members.domain.QMembers(forProperty("members")) : null;
        this.resume = inits.isInitialized("resume") ? new com.hckst.respal.resume.domain.QResume(forProperty("resume"), inits.get("resume")) : null;
    }

}

