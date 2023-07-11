package repick.repickserver.domain.order.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSellOrder is a Querydsl query type for SellOrder
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSellOrder extends EntityPathBase<SellOrder> {

    private static final long serialVersionUID = -1154979809L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSellOrder sellOrder = new QSellOrder("sellOrder");

    public final repick.repickserver.domain.model.QBaseTimeEntity _super = new repick.repickserver.domain.model.QBaseTimeEntity(this);

    public final StringPath accountNumber = createString("accountNumber");

    public final repick.repickserver.domain.member.domain.QAddress address;

    public final NumberPath<Integer> bagQuantity = createNumber("bagQuantity", Integer.class);

    public final StringPath bankName = createString("bankName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final repick.repickserver.domain.member.domain.QMember member;

    public final StringPath name = createString("name");

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final StringPath phoneNumber = createString("phoneNumber");

    public final NumberPath<Integer> productQuantity = createNumber("productQuantity", Integer.class);

    public final StringPath requestDetail = createString("requestDetail");

    public final DateTimePath<java.time.LocalDateTime> returnDate = createDateTime("returnDate", java.time.LocalDateTime.class);

    public final EnumPath<SellState> sellState = createEnum("sellState", SellState.class);

    public QSellOrder(String variable) {
        this(SellOrder.class, forVariable(variable), INITS);
    }

    public QSellOrder(Path<? extends SellOrder> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSellOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSellOrder(PathMetadata metadata, PathInits inits) {
        this(SellOrder.class, metadata, inits);
    }

    public QSellOrder(Class<? extends SellOrder> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.address = inits.isInitialized("address") ? new repick.repickserver.domain.member.domain.QAddress(forProperty("address")) : null;
        this.member = inits.isInitialized("member") ? new repick.repickserver.domain.member.domain.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

