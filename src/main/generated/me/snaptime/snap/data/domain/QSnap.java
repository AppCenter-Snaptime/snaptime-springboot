package me.snaptime.snap.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSnap is a Querydsl query type for Snap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSnap extends EntityPathBase<Snap> {

    private static final long serialVersionUID = 564236851L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSnap snap = new QSnap("snap");

    public final me.snaptime.common.domain.QBaseTimeEntity _super = new me.snaptime.common.domain.QBaseTimeEntity(this);

    public final QAlbum album;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> Id = createNumber("Id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath oneLineJournal = createString("oneLineJournal");

    public final QPhoto photo;

    public final ListPath<me.snaptime.social.data.domain.Reply, me.snaptime.social.data.domain.QReply> replyList = this.<me.snaptime.social.data.domain.Reply, me.snaptime.social.data.domain.QReply>createList("replyList", me.snaptime.social.data.domain.Reply.class, me.snaptime.social.data.domain.QReply.class, PathInits.DIRECT2);

    public final me.snaptime.user.data.domain.QUser user;

    public QSnap(String variable) {
        this(Snap.class, forVariable(variable), INITS);
    }

    public QSnap(Path<? extends Snap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSnap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSnap(PathMetadata metadata, PathInits inits) {
        this(Snap.class, metadata, inits);
    }

    public QSnap(Class<? extends Snap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.album = inits.isInitialized("album") ? new QAlbum(forProperty("album")) : null;
        this.photo = inits.isInitialized("photo") ? new QPhoto(forProperty("photo")) : null;
        this.user = inits.isInitialized("user") ? new me.snaptime.user.data.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

