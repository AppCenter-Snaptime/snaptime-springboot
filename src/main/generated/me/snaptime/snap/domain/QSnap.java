package me.snaptime.snap.domain;

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

    private static final long serialVersionUID = -1248874159L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSnap snap = new QSnap("snap");

    public final me.snaptime.common.QBaseTimeEntity _super = new me.snaptime.common.QBaseTimeEntity(this);

    public final me.snaptime.album.domain.QAlbum album;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath fileName = createString("fileName");

    public final StringPath filePath = createString("filePath");

    public final StringPath fileType = createString("fileType");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isPrivate = createBoolean("isPrivate");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath oneLineJournal = createString("oneLineJournal");

    public final me.snaptime.user.domain.QUser user;

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
        this.album = inits.isInitialized("album") ? new me.snaptime.album.domain.QAlbum(forProperty("album"), inits.get("album")) : null;
        this.user = inits.isInitialized("user") ? new me.snaptime.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

