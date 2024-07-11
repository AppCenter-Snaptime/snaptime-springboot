package me.snaptime.reply.domain;

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

    private static final long serialVersionUID = -1906507021L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChildReply childReply = new QChildReply("childReply");

    public final me.snaptime.common.QBaseTimeEntity _super = new me.snaptime.common.QBaseTimeEntity(this);

    public final NumberPath<Long> childReplyId = createNumber("childReplyId", Long.class);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QParentReply parentReply;

    public final me.snaptime.user.domain.QUser replyTagUser;

    public final me.snaptime.user.domain.QUser user;

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
        this.replyTagUser = inits.isInitialized("replyTagUser") ? new me.snaptime.user.domain.QUser(forProperty("replyTagUser"), inits.get("replyTagUser")) : null;
        this.user = inits.isInitialized("user") ? new me.snaptime.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

