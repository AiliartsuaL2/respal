package com.hckst.respal.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRole is a Querydsl query type for Role
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRole extends EntityPathBase<Role> {

    private static final long serialVersionUID = -37576265L;

    public static final QRole role = new QRole("role");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<Members, QMembers> membersList = this.<Members, QMembers>createList("membersList", Members.class, QMembers.class, PathInits.DIRECT2);

    public final EnumPath<com.hckst.respal.common.converter.RoleType> roles = createEnum("roles", com.hckst.respal.common.converter.RoleType.class);

    public QRole(String variable) {
        super(Role.class, forVariable(variable));
    }

    public QRole(Path<? extends Role> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRole(PathMetadata metadata) {
        super(Role.class, metadata);
    }

}

