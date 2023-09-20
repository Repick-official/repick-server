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
}
