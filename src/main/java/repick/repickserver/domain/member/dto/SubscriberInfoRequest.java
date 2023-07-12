package repick.repickserver.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberInfoRequest {

    private Long id;
    private String userId;
    private LocalDateTime expireDate;

}
