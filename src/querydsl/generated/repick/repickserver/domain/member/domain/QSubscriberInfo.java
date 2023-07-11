package repick.repickserver.domain.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubscriberInfo is a Querydsl query type for SubscriberInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubscriberInfo extends EntityPathBase<SubscriberInfo> {

    private static final long serialVersionUID = 2070947033L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubscriberInfo subscriberInfo = new QSubscriberInfo("subscriberInfo");

    public final repick.repickserver.domain.model.QBaseTimeEntity _super = new repick.repickserver.domain.model.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> expireDate = createDateTime("expireDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QMember member;

    public final EnumPath<SubscribeState> subscribeState = createEnum("subscribeState", SubscribeState.class);

    public QSubscriberInfo(String variable) {
        this(SubscriberInfo.class, forVariable(variable), INITS);
    }

    public QSubscriberInfo(Path<? extends SubscriberInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubscriberInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubscriberInfo(PathMetadata metadata, PathInits inits) {
        this(SubscriberInfo.class, metadata, inits);
    }

    public QSubscriberInfo(Class<? extends SubscriberInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member"), inits.get("member")) : null;
    }

}

