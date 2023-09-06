package repick.repickserver.domain.sellorder.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import repick.repickserver.domain.sellorder.domain.SellOrderState;
import repick.repickserver.domain.sellorder.domain.SellState;

import java.util.Objects;

import static repick.repickserver.domain.sellorder.domain.QSellOrderState.sellOrderState;

@RequiredArgsConstructor
@Repository
public class SellOrderStateRepositoryImpl implements SellOrderStateRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public SellOrderState findLastStateBySellOrderId(Long sellOrderId) {
        // 판매 주문 아이디가 매치하고, 마지막으로 수정된(가장 최신의) 데이터를 반환
        return jpaQueryFactory
            .selectFrom(sellOrderState)
            .where(sellOrderState.sellOrder.id.eq(sellOrderId))
            .orderBy(sellOrderState.lastModifiedDate.desc())
            .limit(1)
            .fetchOne();
    }

    @Override
    public boolean isLastBySellOrderId(Long sellOrderId, SellState state) {
        return Objects.requireNonNull(jpaQueryFactory
                .selectFrom(sellOrderState)
                .where(sellOrderState.sellOrder.id.eq(sellOrderId))
                .orderBy(sellOrderState.lastModifiedDate.desc())
                .limit(1)
                .fetchOne()).getSellState() == state;
    }
}
