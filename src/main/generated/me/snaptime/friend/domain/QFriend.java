package me.snaptime.friend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriend is a Querydsl query type for Friend
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriend extends EntityPathBase<Friend> {

    private static final long serialVersionUID = -221967879L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriend friend = new QFriend("friend");

    public final NumberPath<Long> friendId = createNumber("friendId", Long.class);

    public final me.snaptime.user.domain.QUser receiver;

    public final me.snaptime.user.domain.QUser sender;

    public QFriend(String variable) {
        this(Friend.class, forVariable(variable), INITS);
    }

    public QFriend(Path<? extends Friend> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriend(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriend(PathMetadata metadata, PathInits inits) {
        this(Friend.class, metadata, inits);
    }

    public QFriend(Class<? extends Friend> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new me.snaptime.user.domain.QUser(forProperty("receiver"), inits.get("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new me.snaptime.user.domain.QUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

