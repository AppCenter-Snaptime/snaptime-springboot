package me.snaptime.friendShip.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendShip is a Querydsl query type for FriendShip
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendShip extends EntityPathBase<FriendShip> {

    private static final long serialVersionUID = -1572262991L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendShip friendShip = new QFriendShip("friendShip");

    public final NumberPath<Long> friendShipId = createNumber("friendShipId", Long.class);

    public final EnumPath<me.snaptime.friendShip.common.FriendStatus> friendStatus = createEnum("friendStatus", me.snaptime.friendShip.common.FriendStatus.class);

    public final me.snaptime.user.domain.QUser fromUser;

    public final me.snaptime.user.domain.QUser toUser;

    public QFriendShip(String variable) {
        this(FriendShip.class, forVariable(variable), INITS);
    }

    public QFriendShip(Path<? extends FriendShip> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendShip(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendShip(PathMetadata metadata, PathInits inits) {
        this(FriendShip.class, metadata, inits);
    }

    public QFriendShip(Class<? extends FriendShip> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fromUser = inits.isInitialized("fromUser") ? new me.snaptime.user.domain.QUser(forProperty("fromUser"), inits.get("fromUser")) : null;
        this.toUser = inits.isInitialized("toUser") ? new me.snaptime.user.domain.QUser(forProperty("toUser"), inits.get("toUser")) : null;
    }

}

