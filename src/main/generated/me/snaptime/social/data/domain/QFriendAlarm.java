package me.snaptime.social.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendAlarm is a Querydsl query type for FriendAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendAlarm extends EntityPathBase<FriendAlarm> {

    private static final long serialVersionUID = 2130010669L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendAlarm friendAlarm = new QFriendAlarm("friendAlarm");

    public final EnumPath<me.snaptime.social.common.AlarmType> alarmType = createEnum("alarmType", me.snaptime.social.common.AlarmType.class);

    public final me.snaptime.user.data.domain.QUser fromUser;

    public final BooleanPath isRead = createBoolean("isRead");

    public final NumberPath<Long> snapAlarmId = createNumber("snapAlarmId", Long.class);

    public final me.snaptime.user.data.domain.QUser toUser;

    public QFriendAlarm(String variable) {
        this(FriendAlarm.class, forVariable(variable), INITS);
    }

    public QFriendAlarm(Path<? extends FriendAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendAlarm(PathMetadata metadata, PathInits inits) {
        this(FriendAlarm.class, metadata, inits);
    }

    public QFriendAlarm(Class<? extends FriendAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fromUser = inits.isInitialized("fromUser") ? new me.snaptime.user.data.domain.QUser(forProperty("fromUser"), inits.get("fromUser")) : null;
        this.toUser = inits.isInitialized("toUser") ? new me.snaptime.user.data.domain.QUser(forProperty("toUser"), inits.get("toUser")) : null;
    }

}

