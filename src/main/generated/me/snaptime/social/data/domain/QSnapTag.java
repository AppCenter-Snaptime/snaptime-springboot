package me.snaptime.social.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSnapTag is a Querydsl query type for SnapTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSnapTag extends EntityPathBase<SnapTag> {

    private static final long serialVersionUID = 870561866L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSnapTag snapTag = new QSnapTag("snapTag");

    public final me.snaptime.snap.data.domain.QSnap snap;

    public final NumberPath<Long> snapTagId = createNumber("snapTagId", Long.class);

    public final me.snaptime.user.data.domain.QUser tagUser;

    public QSnapTag(String variable) {
        this(SnapTag.class, forVariable(variable), INITS);
    }

    public QSnapTag(Path<? extends SnapTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSnapTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSnapTag(PathMetadata metadata, PathInits inits) {
        this(SnapTag.class, metadata, inits);
    }

    public QSnapTag(Class<? extends SnapTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.snap = inits.isInitialized("snap") ? new me.snaptime.snap.data.domain.QSnap(forProperty("snap"), inits.get("snap")) : null;
        this.tagUser = inits.isInitialized("tagUser") ? new me.snaptime.user.data.domain.QUser(forProperty("tagUser"), inits.get("tagUser")) : null;
    }

}

