package repick.repickserver.domain.sellorder.repository;

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

}
