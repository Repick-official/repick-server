package repick.repickserver.infra.slack.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.application.SubscriberInfoService;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.ordernumber.dao.OrderNumberReository;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.infra.slack.dto.SlackApiDto;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class SlackApiService {

    private final SlackNotifier slackNotifier;
    private final OrderNumberReository orderNumberReository;
    private final SubscriberInfoService subscriberInfoService;


    public void addSubscribeRequest(SlackApiDto request) {
        System.out.println("request = " + request.getOrderNumber());
        if (!orderNumberReository.existsByOrderNumber(request.getOrderNumber())) {
            slackNotifier.sendSubscribeSlackNotification("명령에 실패했습니다. 주문번호가 올바르지 않습니다.");
            throw new CustomException(ORDER_NOT_FOUND);
        }
        slackNotifier.sendSubscribeSlackNotification("구독 승인: " + request.getOrderNumber());
        subscriberInfoService.add(new SubscriberInfoRequest(request.getOrderNumber()));

    }

    public void denySubscribeRequest(SlackApiDto request) {
        if (!orderNumberReository.existsByOrderNumber(request.getOrderNumber())) {
            slackNotifier.sendSubscribeSlackNotification("명령에 실패했습니다. 주문번호가 올바르지 않습니다.");
            throw new CustomException(ORDER_NOT_FOUND);
        }
        slackNotifier.sendSubscribeSlackNotification("구독 거절: " + request.getOrderNumber());
        subscriberInfoService.deny(new SubscriberInfoRequest(request.getOrderNumber()));

    }
}
