package repick.repickserver.infra.sms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SmsResponse {
    String requestId;
    LocalDateTime requestTime;
    String statusCode; // 202: 성공, 그 외: 실패
    String statusName; // success: 성공, fail: 실패
}
