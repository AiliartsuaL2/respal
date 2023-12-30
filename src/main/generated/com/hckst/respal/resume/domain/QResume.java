package com.hckst.respal.resume.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResume is a Querydsl query type for Resume
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResume extends EntityPathBase<Resume> {

    private static final long serialVersionUID = 611266655L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResume resume = new QResume("resume");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> deleteTime = createDateTime("deleteTime", java.time.LocalDateTime.class);

    public final EnumPath<com.hckst.respal.converter.TFCode> deleteYn = createEnum("deleteYn", com.hckst.respal.converter.TFCode.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.hckst.respal.members.domain.QMembers members;

    public final DateTimePath<java.time.LocalDateTime> modifyTime = createDateTime("modifyTime", java.time.LocalDateTime.class);

    public final EnumPath<com.hckst.respal.converter.TFCode> modifyYn = createEnum("modifyYn", com.hckst.respal.converter.TFCode.class);

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final QResumeFile resumeFile;

    public final EnumPath<com.hckst.respal.converter.ResumeType> resumeType = createEnum("resumeType", com.hckst.respal.converter.ResumeType.class);

    public final ListPath<com.hckst.respal.tag.domain.Tag, com.hckst.respal.tag.domain.QTag> tagList = this.<com.hckst.respal.tag.domain.Tag, com.hckst.respal.tag.domain.QTag>createList("tagList", com.hckst.respal.tag.domain.Tag.class, com.hckst.respal.tag.domain.QTag.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> views = createNumber("views", Integer.class);

    public QResume(String variable) {
        this(Resume.class, forVariable(variable), INITS);
    }

    public QResume(Path<? extends Resume> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QResume(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QResume(PathMetadata metadata, PathInits inits) {
        this(Resume.class, metadata, inits);
    }

    public QResume(Class<? extends Resume> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.members = inits.isInitialized("members") ? new com.hckst.respal.members.domain.QMembers(forProperty("members")) : null;
        this.resumeFile = inits.isInitialized("resumeFile") ? new QResumeFile(forProperty("resumeFile")) : null;
    }

}

