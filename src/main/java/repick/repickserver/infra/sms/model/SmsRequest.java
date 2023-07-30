package repick.repickserver.infra.sms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class SmsRequest {
    String type; // SMS Type (SMS, LMS, MMS)
    String contentType; // 메시지 Type (COMM, AD)
    String countryCode;
    String from;
    String content; // 기본 메시지 내용
    List<Message> messages;
}
