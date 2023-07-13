package repick.repickserver.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.member.domain.SubscribeState;

import java.time.LocalDateTime;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfoResponse {
    private Long id;
    private String orderNumber;
    private LocalDateTime expireDate;
    private LocalDateTime createdDate;
    private SubscribeState subscribeState;

}
