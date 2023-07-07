package repick.repickserver.domain.member.domain;

public enum SubscribeState {
    REQUESTED, APPROVED, DENIED // expireDate와 무관하게 요청, 승인, 거절 여부를 나타낸다. 내부 로직에서 현재시간과 expireDate 비교 필수
    
}
