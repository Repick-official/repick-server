package repick.repickserver.infra.slack.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import repick.repickserver.domain.ordernumber.repository.OrderNumberReository;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.infra.slack.application.SlackNotifier;
import repick.repickserver.infra.slack.domain.WebHookType;

import static repick.repickserver.global.error.exception.ErrorCode.ORDER_NOT_FOUND;

@Component @RequiredArgsConstructor
public class SlackApiValidator {

    private final OrderNumberReository orderNumberReository;
    private final SlackNotifier slackNotifier;

    public void validateOrderNumber(WebHookType webHookType, String orderNumber) {
        if (!orderNumberReository.existsByOrderNumber(orderNumber)) {
            slackNotifier.sendSlackNotification(webHookType, "명령에 실패했습니다. 주문번호가 올바르지 않습니다.");
            throw new CustomException(ORDER_NOT_FOUND);
        }
    }

}
