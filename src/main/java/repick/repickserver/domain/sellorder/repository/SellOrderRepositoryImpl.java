package repick.repickserver.domain.sellorder.repository;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import repick.repickserver.domain.sellorder.domain.QSellOrderState;
import repick.repickserver.domain.sellorder.domain.SellState;
import repick.repickserver.domain.sellorder.dto.QSellOrderResponse;
import repick.repickserver.domain.sellorder.dto.SellOrderResponse;

import java.util.List;

import static repick.repickserver.domain.sellorder.domain.QSellOrder.sellOrder;
import static repick.repickserver.domain.sellorder.domain.QSellOrderState.sellOrderState;


@RequiredArgsConstructor
@Repository
public class SellOrderRepositoryImpl implements SellOrderRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SellOrderResponse> getSellOrderResponseById(Long id) {
        return jpaQueryFactory
            .select(new QSellOrderResponse(sellOrder))
            .from(sellOrder)
            .where(sellOrder.member.id.eq(id))
            .fetch();
    }

    @Override
    public List<SellOrderResponse> getSellOrdersByMemberIdAndState(Long memberId, SellState state) {
        QSellOrderState subSellOrderState = new QSellOrderState("subSellOrderState");

        return jpaQueryFactory
                .select(new QSellOrderResponse(sellOrder))
                .from(sellOrder)
                .innerJoin(sellOrder.sellOrderStates, sellOrderState)
                .where(sellOrder.member.id.eq(memberId))
                .where(sellOrderState.createdDate.eq(
                        JPAExpressions
                                .select(subSellOrderState.createdDate.max())
                                .from(subSellOrderState)
                                .where(subSellOrderState.sellOrder.eq(sellOrder))
                ))
                .where(sellOrderState.sellState.eq(state))
                .fetch();
    }

    @Override
    public List<SellOrderResponse> getSellOrdersByState(SellState state) {
        QSellOrderState subSellOrderState = new QSellOrderState("subSellOrderState");

        return jpaQueryFactory
                .select(new QSellOrderResponse(sellOrder))
                .from(sellOrder)
                .innerJoin(sellOrder.sellOrderStates, sellOrderState)
                .where(sellOrderState.createdDate.eq(
                        JPAExpressions
                                .select(subSellOrderState.createdDate.max())
                                .from(subSellOrderState)
                                .where(subSellOrderState.sellOrder.eq(sellOrder))
                ))
                .where(sellOrderState.sellState.eq(state))
                .fetch();
    }

    @Override
    public Long countBySellState(SellState requestedState) {

        // 각 order 별 가장 최신의 orderState id를 구함
        SubQueryExpression<Long> maxIdSubQuery = JPAExpressions
                .select(sellOrderState.id.max())
                .from(sellOrderState)
                .groupBy(sellOrderState.sellOrder.id);

        return jpaQueryFactory
                .select(sellOrder.count())
                .from(sellOrder)
                .leftJoin(sellOrderState)
                .on(sellOrder.id.eq(sellOrderState.sellOrder.id))
                .where(sellOrderState.id.in(maxIdSubQuery),
                        sellOrderState.sellState.eq(requestedState))
                .fetchOne();

    }

    @Override
    public Boolean existsBySellOrderIdAndSellState(Long sellOrderId, SellState state) {
        // 각 order 별 가장 최신의 orderState id를 구함
        SubQueryExpression<Long> maxIdSubQuery = JPAExpressions
                .select(sellOrderState.id.max())
                .from(sellOrderState)
                .groupBy(sellOrderState.sellOrder.id);

        return jpaQueryFactory
                .select(sellOrderState.id)
                .from(sellOrderState)
                .where(sellOrderState.id.in(maxIdSubQuery),
                        sellOrderState.sellState.eq(state),
                        sellOrderState.sellOrder.id.eq(sellOrderId))
                .fetchOne() != null;

    }

}
