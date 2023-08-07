package com.hckst.respal.mention.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMention is a Querydsl query type for Mention
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMention extends EntityPathBase<Mention> {

    private static final long serialVersionUID = -715876211L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMention mention = new QMention("mention");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.hckst.respal.members.domain.QMembers members;

    public final DateTimePath<java.time.LocalDateTime> regTime = createDateTime("regTime", java.time.LocalDateTime.class);

    public final com.hckst.respal.resume.domain.QResume resume;

    public QMention(String variable) {
        this(Mention.class, forVariable(variable), INITS);
    }

    public QMention(Path<? extends Mention> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMention(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMention(PathMetadata metadata, PathInits inits) {
        this(Mention.class, metadata, inits);
    }

    public QMention(Class<? extends Mention> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.members = inits.isInitialized("members") ? new com.hckst.respal.members.domain.QMembers(forProperty("members"), inits.get("members")) : null;
        this.resume = inits.isInitialized("resume") ? new com.hckst.respal.resume.domain.QResume(forProperty("resume"), inits.get("resume")) : null;
    }

}

