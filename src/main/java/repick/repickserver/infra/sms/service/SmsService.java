package repick.repickserver.infra.sms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import repick.repickserver.global.Parser;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.infra.sms.SmsSender;
import repick.repickserver.infra.sms.model.Message;

import static repick.repickserver.global.error.exception.ErrorCode.SMS_SEND_FAILED;
import static repick.repickserver.global.util.formatPhoneNumber.removeHyphens;

@Slf4j
@Component @RequiredArgsConstructor
public class SmsService {

    private final SmsSender smsSender;

    @Async
    public void BeforeSmsProductSender(String phoneNumber, int quantity, String name) {
        try {
            smsSender.sendSms(Message.builder()
                    .to(removeHyphens(phoneNumber))
                    .content(Parser.parseProductListToString(quantity, name))
                    .build());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(SMS_SEND_FAILED);
        }
    }

    @Async
    public void BagPendingSender(String name, String phoneNumber) {

        String content = "[리픽]\n\n" +
                "안녕하세요, " + name + "님 :)\n" +
                "옷장 정리를 신청해주셔서 감사합니다.\n" +
                "리픽백은 발송 후 수일 후 배송이 완료됩니다.\n" +
                "감사합니다 ♥";

        try {
            smsSender.sendSms(Message.builder()
                    .to(removeHyphens(phoneNumber))
                    .content(content)
                    .build());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(SMS_SEND_FAILED);
        }
    }

}
