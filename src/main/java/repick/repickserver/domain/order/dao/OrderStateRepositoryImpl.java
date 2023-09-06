package repick.repickserver.domain.order.dao;

import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import repick.repickserver.domain.order.domain.OrderCurrentState;
import repick.repickserver.domain.order.dto.OrderStateResponse;
import repick.repickserver.domain.order.dto.QOrderStateResponse;

import java.util.List;

import static repick.repickserver.domain.order.domain.QOrder.order;
import static repick.repickserver.domain.order.domain.QOrderState.orderState;


@RequiredArgsConstructor
public class OrderStateRepositoryImpl implements OrderStateRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 전체 주문 상태 조회 (관리자)
     * 상태별로 필터링 가능
     */
    public List<OrderStateResponse> getOrderStates(String requestedOrderState) {
        // 각 order 별 가장 최신의 orderState id를 구함
        SubQueryExpression<Long> maxIdSubQuery = JPAExpressions
                .select(orderState.id.max())
                .from(orderState)
                .groupBy(orderState.order.id);

        // order 리스트, 각 order 별 가장 최신의 orderState 리스트
        return jpaQueryFactory
                .select(new QOrderStateResponse(order, orderState))
                .from(order)
                .leftJoin(orderState)
                .on(order.id.eq(orderState.order.id))
                .where(orderState.id.in(maxIdSubQuery),
                        eqOrderState(requestedOrderState))
                .fetch();
    }

    BooleanExpression eqOrderState(String requestedOrderState) {
        if (requestedOrderState == null) {
            return null;
        }
        return orderState.orderCurrentState.eq(OrderCurrentState.valueOf(requestedOrderState));
    }
}
