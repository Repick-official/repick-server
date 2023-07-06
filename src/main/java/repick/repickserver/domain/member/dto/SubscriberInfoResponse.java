package repick.repickserver.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfoResponse {
    private LocalDateTime expireDate;
    private LocalDateTime createdAt;

}
