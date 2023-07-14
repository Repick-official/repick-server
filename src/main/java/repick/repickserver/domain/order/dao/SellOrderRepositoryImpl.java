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
    public List<SellOrder> getSellOrdersById(Long id) {

        // 멤버의 id로 판매 주문 조회
        return jpaQueryFactory
            .selectFrom(sellOrder)
            .where(sellOrder.member.id.eq(id))
            .fetch();
    }

    @Override
    public List<SellOrder> getSellOrdersByIdAndState(Long id, SellState state) {
        return jpaQueryFactory
            .selectFrom(sellOrder)
            // 멤버의 id로 판매 주문 조회
            .where(sellOrder.member.id.eq(id)
            // 판매 주문의 상태가 하나라도 매치하는 판매 주문 조회
            .and(sellOrder.sellOrderStates.any().sellState.eq(state)))
            .fetch();

    }

    @Override
    public List<SellOrder> getSellOrdersByState(SellState state) {
        // sellOrderStates의 sellState가 매치하는 판매 주문 조회
        return jpaQueryFactory
            .selectFrom(sellOrder)
            .where(sellOrder.sellOrderStates.any().sellState.eq(state))
            .fetch();
    }
}
