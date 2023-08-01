package com.hckst.respal.resume.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QResumeFile is a Querydsl query type for ResumeFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResumeFile extends EntityPathBase<ResumeFile> {

    private static final long serialVersionUID = -21802373L;

    public static final QResumeFile resumeFile = new QResumeFile("resumeFile");

    public final StringPath accessUrl = createString("accessUrl");

    public final DateTimePath<java.time.LocalDateTime> deleteTime = createDateTime("deleteTime", java.time.LocalDateTime.class);

    public final EnumPath<com.hckst.respal.converter.TFCode> deleteYn = createEnum("deleteYn", com.hckst.respal.converter.TFCode.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath originName = createString("originName");

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final StringPath storedName = createString("storedName");

    public QResumeFile(String variable) {
        super(ResumeFile.class, forVariable(variable));
    }

    public QResumeFile(Path<? extends ResumeFile> path) {
        super(path.getType(), path.getMetadata());
    }

    public QResumeFile(PathMetadata metadata) {
        super(ResumeFile.class, metadata);
    }

}

