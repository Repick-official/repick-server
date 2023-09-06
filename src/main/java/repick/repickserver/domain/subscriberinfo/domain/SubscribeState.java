package repick.repickserver.domain.subscriberinfo.domain;

public enum SubscribeState {
    REQUESTED, APPROVED, DENIED, // expireDate와 무관하게 요청, 승인, 거절 여부를 나타낸다. 내부 로직에서 현재시간과 expireDate 비교 필수
    REQUEST_EXPIRED, EXPIRED // 프론트에게 넘겨주기 위해 만든 상태로 실제로는 존재하지 않는다.
    
}
