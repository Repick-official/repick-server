package repick.repickserver.domain.order.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import repick.repickserver.domain.order.domain.SellOrder;
import repick.repickserver.domain.order.domain.SellState;

import java.util.List;

import static repick.repickserver.domain.order.domain.QSellOrder.sellOrder;


@RequiredArgsConstructor
@Repository
public class SellOrderRepositoryImpl implements SellOrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SellOrder> getSellOrders(Long id, SellState state) {

        // get sell orders by id and state
        return jpaQueryFactory
            .selectFrom(sellOrder)
            .where(sellOrder.member.id.eq(id)
            .and(sellOrder.sellState.eq(state)))
            .fetch();
    }

    @Override
    public List<SellOrder> getRequestedSellOrders() {
        return jpaQueryFactory
            .selectFrom(sellOrder)
            .where(sellOrder.sellState.eq(SellState.REQUESTED))
            .fetch();
    }
}
