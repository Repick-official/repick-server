package repick.repickserver.domain.order.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = -1265672751L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final repick.repickserver.domain.model.QBaseTimeEntity _super = new repick.repickserver.domain.model.QBaseTimeEntity(this);

    public final repick.repickserver.domain.member.domain.QAddress address;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final repick.repickserver.domain.member.domain.QMember member;

    public final StringPath name = createString("name");

    public final EnumPath<OrderState> orderState = createEnum("orderState", OrderState.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Integer> quantity = createNumber("quantity", Integer.class);

    public final StringPath requestDetail = createString("requestDetail");

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new repick.repickserver.domain.member.domain.QAddress(forProperty("address")) : null;
        this.member = inits.isInitialized("member") ? new repick.repickserver.domain.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

