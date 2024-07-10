package me.snaptime.profile.profilePhoto.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfilePhoto is a Querydsl query type for ProfilePhoto
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfilePhoto extends EntityPathBase<ProfilePhoto> {

    private static final long serialVersionUID = -740751628L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfilePhoto profilePhoto = new QProfilePhoto("profilePhoto");

    public final me.snaptime.common.QBaseTimeEntity _super = new me.snaptime.common.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath profilePhotoName = createString("profilePhotoName");

    public final StringPath profilePhotoPath = createString("profilePhotoPath");

    public final me.snaptime.user.domain.QUser user;

    public QProfilePhoto(String variable) {
        this(ProfilePhoto.class, forVariable(variable), INITS);
    }

    public QProfilePhoto(Path<? extends ProfilePhoto> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfilePhoto(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfilePhoto(PathMetadata metadata, PathInits inits) {
        this(ProfilePhoto.class, metadata, inits);
    }

    public QProfilePhoto(Class<? extends ProfilePhoto> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new me.snaptime.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

