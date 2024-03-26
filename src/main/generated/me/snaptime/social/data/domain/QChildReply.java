package me.snaptime.social.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChildReply is a Querydsl query type for ChildReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChildReply extends EntityPathBase<ChildReply> {

    private static final long serialVersionUID = 951888916L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChildReply childReply = new QChildReply("childReply");

    public final me.snaptime.common.domain.QBaseTimeEntity _super = new me.snaptime.common.domain.QBaseTimeEntity(this);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QParentReply parentReply;

    public final me.snaptime.user.data.domain.QUser tagUser;

    public final me.snaptime.user.data.domain.QUser user;

    public QChildReply(String variable) {
        this(ChildReply.class, forVariable(variable), INITS);
    }

    public QChildReply(Path<? extends ChildReply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QChildReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QChildReply(PathMetadata metadata, PathInits inits) {
        this(ChildReply.class, metadata, inits);
    }

    public QChildReply(Class<? extends ChildReply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentReply = inits.isInitialized("parentReply") ? new QParentReply(forProperty("parentReply"), inits.get("parentReply")) : null;
        this.tagUser = inits.isInitialized("tagUser") ? new me.snaptime.user.data.domain.QUser(forProperty("tagUser"), inits.get("tagUser")) : null;
        this.user = inits.isInitialized("user") ? new me.snaptime.user.data.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

