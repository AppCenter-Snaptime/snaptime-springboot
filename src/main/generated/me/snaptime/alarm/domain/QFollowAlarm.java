package me.snaptime.alarm.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFollowAlarm is a Querydsl query type for FollowAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFollowAlarm extends EntityPathBase<FollowAlarm> {

    private static final long serialVersionUID = -1019878604L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFollowAlarm followAlarm = new QFollowAlarm("followAlarm");

    public final EnumPath<me.snaptime.alarm.common.AlarmType> alarmType = createEnum("alarmType", me.snaptime.alarm.common.AlarmType.class);

    public final NumberPath<Long> followAlarmId = createNumber("followAlarmId", Long.class);

    public final BooleanPath isRead = createBoolean("isRead");

    public final me.snaptime.user.domain.QUser receiver;

    public final me.snaptime.user.domain.QUser sender;

    public QFollowAlarm(String variable) {
        this(FollowAlarm.class, forVariable(variable), INITS);
    }

    public QFollowAlarm(Path<? extends FollowAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFollowAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFollowAlarm(PathMetadata metadata, PathInits inits) {
        this(FollowAlarm.class, metadata, inits);
    }

    public QFollowAlarm(Class<? extends FollowAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new me.snaptime.user.domain.QUser(forProperty("receiver"), inits.get("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new me.snaptime.user.domain.QUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

