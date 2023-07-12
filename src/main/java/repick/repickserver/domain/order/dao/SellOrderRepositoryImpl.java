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
            .and(sellOrder.sellState.eq(state))
            .and(sellOrder.childSellOrders.isEmpty()))
            .fetch();
    }

    @Override
    public List<SellOrder> getSellOrdersAdmin(SellState state) {
        return jpaQueryFactory
            .selectFrom(sellOrder)
            // REQUESTED 상태인 판매 주문이고, childSellOrders가 비어있는 판매 주문만 조회
            .where(sellOrder.sellState.eq(state)
            .and(sellOrder.childSellOrders.isEmpty()))
            .fetch();
    }
}
