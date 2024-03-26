package me.snaptime.social.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QParentReply is a Querydsl query type for ParentReply
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QParentReply extends EntityPathBase<ParentReply> {

    private static final long serialVersionUID = 1772479546L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QParentReply parentReply = new QParentReply("parentReply");

    public final me.snaptime.common.domain.QBaseTimeEntity _super = new me.snaptime.common.domain.QBaseTimeEntity(this);

    public final ListPath<ChildReply, QChildReply> childReplyList = this.<ChildReply, QChildReply>createList("childReplyList", ChildReply.class, QChildReply.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final me.snaptime.snap.data.domain.QSnap snap;

    public final me.snaptime.user.data.domain.QUser user;

    public QParentReply(String variable) {
        this(ParentReply.class, forVariable(variable), INITS);
    }

    public QParentReply(Path<? extends ParentReply> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QParentReply(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QParentReply(PathMetadata metadata, PathInits inits) {
        this(ParentReply.class, metadata, inits);
    }

    public QParentReply(Class<? extends ParentReply> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.snap = inits.isInitialized("snap") ? new me.snaptime.snap.data.domain.QSnap(forProperty("snap"), inits.get("snap")) : null;
        this.user = inits.isInitialized("user") ? new me.snaptime.user.data.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

