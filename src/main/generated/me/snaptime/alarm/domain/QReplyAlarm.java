package me.snaptime.alarm.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReplyAlarm is a Querydsl query type for ReplyAlarm
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReplyAlarm extends EntityPathBase<ReplyAlarm> {

    private static final long serialVersionUID = -383534861L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReplyAlarm replyAlarm = new QReplyAlarm("replyAlarm");

    public final EnumPath<me.snaptime.alarm.common.AlarmType> alarmType = createEnum("alarmType", me.snaptime.alarm.common.AlarmType.class);

    public final BooleanPath isRead = createBoolean("isRead");

    public final StringPath messgae = createString("messgae");

    public final me.snaptime.user.domain.QUser receiver;

    public final NumberPath<Long> replyAlarmId = createNumber("replyAlarmId", Long.class);

    public final me.snaptime.user.domain.QUser sender;

    public final me.snaptime.snap.domain.QSnap snap;

    public QReplyAlarm(String variable) {
        this(ReplyAlarm.class, forVariable(variable), INITS);
    }

    public QReplyAlarm(Path<? extends ReplyAlarm> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReplyAlarm(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReplyAlarm(PathMetadata metadata, PathInits inits) {
        this(ReplyAlarm.class, metadata, inits);
    }

    public QReplyAlarm(Class<? extends ReplyAlarm> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new me.snaptime.user.domain.QUser(forProperty("receiver"), inits.get("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new me.snaptime.user.domain.QUser(forProperty("sender"), inits.get("sender")) : null;
        this.snap = inits.isInitialized("snap") ? new me.snaptime.snap.domain.QSnap(forProperty("snap"), inits.get("snap")) : null;
    }

}

