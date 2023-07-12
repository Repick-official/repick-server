package repick.repickserver.domain.order.domain;

public enum SellState {
    REQUESTED, CANCELLED, DELIVERED, PUBLISHED,
    RETURN_REQUESTED, RETURNING, RETURNED // 판매자가 판매 취소 요청하였을 경우.. MVP 미구현

}
