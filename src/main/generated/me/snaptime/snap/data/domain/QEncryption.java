package me.snaptime.snap.data.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEncryption is a Querydsl query type for Encryption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEncryption extends EntityPathBase<Encryption> {

    private static final long serialVersionUID = 787447596L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEncryption encryption = new QEncryption("encryption");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SimplePath<javax.crypto.SecretKey> secretKey = createSimple("secretKey", javax.crypto.SecretKey.class);

    public final me.snaptime.user.data.domain.QUser user;

    public QEncryption(String variable) {
        this(Encryption.class, forVariable(variable), INITS);
    }

    public QEncryption(Path<? extends Encryption> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEncryption(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEncryption(PathMetadata metadata, PathInits inits) {
        this(Encryption.class, metadata, inits);
    }

    public QEncryption(Class<? extends Encryption> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new me.snaptime.user.data.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

