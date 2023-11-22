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

    public void sendSellOrderSms(String phoneNumber) {
        String content = "[리픽]\n\n" +
                "안녕하세요, 리픽입니다.\n" +
                "리픽백을 신청해 주셔서 감사드려요!\n" +
                "수거 신청 절차 안내해 드립니다.\n" +
                "-----------------------------\n" +
                "▶ '리픽 홈페이지'에서 수거 신청하기\n" +
                "1. 리픽백이 도착하면 판매하고 싶은 옷을 모두 담아주세요.\n" +
                "2. 리픽백을 받으신 곳에 놓아주세요.\n" +
                "3. 아래 링크를 통해 홈페이지 접속 → 옷장정리 → '리픽백 배출' 을 누르면 끝!\n" +
                "안전하게 수거하여 판매해드릴게요 :) \n" +
                "▶ 리픽 이용 꿀팁!\n" +
                "계절에 맞는 겨울 옷을 보내주시면 판매가 더 잘 돼요!\n" +
                "최근에 구매한 옷은 인기가 더 많아요!\n" +
                "-----------------------------\n" +
                "▶ 리픽 홈페이지\n" +
                "app.repick.seoul.kr";

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
