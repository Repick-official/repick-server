package repick.repickserver.infra.sms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Message {
    String to;
    String content; // 개별 메시지 내용
}
