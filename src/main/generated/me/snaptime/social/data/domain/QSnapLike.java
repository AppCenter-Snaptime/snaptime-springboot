package me.snaptime.social.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSnapLike is a Querydsl query type for SnapLike
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSnapLike extends EntityPathBase<SnapLike> {

    private static final long serialVersionUID = 1217383655L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSnapLike snapLike = new QSnapLike("snapLike");

    public final me.snaptime.snap.data.domain.QSnap snap;

    public final NumberPath<Long> snapLikeId = createNumber("snapLikeId", Long.class);

    public final me.snaptime.user.data.domain.QUser user;

    public QSnapLike(String variable) {
        this(SnapLike.class, forVariable(variable), INITS);
    }

    public QSnapLike(Path<? extends SnapLike> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSnapLike(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSnapLike(PathMetadata metadata, PathInits inits) {
        this(SnapLike.class, metadata, inits);
    }

    public QSnapLike(Class<? extends SnapLike> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.snap = inits.isInitialized("snap") ? new me.snaptime.snap.data.domain.QSnap(forProperty("snap"), inits.get("snap")) : null;
        this.user = inits.isInitialized("user") ? new me.snaptime.user.data.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

