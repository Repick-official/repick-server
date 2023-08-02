package repick.repickserver.domain.member.application;

import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.SubscriberInfoRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;
import repick.repickserver.domain.member.dto.SubscribeHistoryResponse;
import repick.repickserver.domain.member.dto.SubscriberInfoRegisterRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;
import repick.repickserver.domain.member.mapper.SubscriberInfoMapper;
import repick.repickserver.domain.member.validator.MemberValidator;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.global.Parser;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.infra.slack.application.SlackNotifier;
import repick.repickserver.infra.slack.mapper.SlackMapper;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriberInfoService {

    private final SubscriberInfoRepository subscriberInfoRepository;
    private final JwtProvider jwtProvider;
    private final OrderNumberService orderNumberService;
    private final SlackNotifier slackNotifier;
    private final MemberValidator memberValidator;
    private final SubscriberInfoMapper subscriberInfoMapper;
    private final SlackMapper slackMapper;

    public List<SubscribeHistoryResponse> historyAll(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);

        List<SubscriberInfo> subscriberInfos = subscriberInfoRepository.findSubscriberInfoByMemberId(member.getId());

        return subscriberInfos.stream().map(subscriberInfo -> {
            assert subscriberInfo.getExpireDate() != null;

            // 공통된 부분을 먼저 설정
            SubscribeHistoryResponse.SubscribeHistoryResponseBuilder responseBuilder = SubscribeHistoryResponse.builder()
                    .orderNumber(subscriberInfo.getOrderNumber())
                    .createdDate(subscriberInfo.getCreatedDate())
                    .expireDate(subscriberInfo.getExpireDate())
                    .subscribeType(subscriberInfo.getSubscribeType());

            // 만료일에 따라서 상태를 결정
            if (subscriberInfo.getExpireDate().isBefore(LocalDateTime.now())) {
                responseBuilder.subscribeState(SubscribeState.EXPIRED);
            } else {
                responseBuilder.subscribeState(subscriberInfo.getSubscribeState());
            }

            return responseBuilder.build();
        }).collect(Collectors.toList());

    }

    public List<SubscribeHistoryResponse> history(String state, String token) {
        Pair<SubscribeState, Boolean> subscribeState = Parser.parseSubscribeState(state);

        Member member = jwtProvider.getMemberByRawToken(token);

        List<SubscriberInfo> subscriberInfos = subscriberInfoRepository.findSubscriberInfoByMemberIdAndState(
                member.getId(),
                subscribeState.getFirst(),
                subscribeState.getSecond());

        return subscriberInfos.stream().map(subscriberInfoMapper::toSubscribeHistoryResponse)
                .collect(Collectors.toList());
    }

    public List<SubscriberInfoResponse> getRequestedSubscriberInfos() {
        return subscriberInfoRepository.findValidRequests().stream()
                .map(subscriberInfoMapper::toSubscriberInfoResponse)
                .collect(Collectors.toList());
    }

    private SubscriberInfoResponse handleRequest(SubscriberInfoRequest request, SubscribeState newState, LocalDateTime expireDate) {
        SubscriberInfo parent = subscriberInfoRepository.findByOrderNumberAndSubscribeState(request.getOrderNumber(), SubscribeState.REQUESTED);

        SubscriberInfo subscriberInfo = subscriberInfoMapper.toSubscriberInfo(parent, newState, expireDate);

        subscriberInfoRepository.save(subscriberInfo);

        return subscriberInfoMapper.toSubscriberInfoResponse(subscriberInfo);
    }

    public SubscriberInfoResponse add(SubscriberInfoRequest request) {
        return handleRequest(request, SubscribeState.APPROVED, LocalDateTime.now().plusMonths(1));
    }

    public SubscriberInfoResponse deny(SubscriberInfoRequest request) {
        return handleRequest(request, SubscribeState.DENIED, LocalDateTime.now());
    }

    public SubscriberInfoResponse subscribeRequest(String token, SubscriberInfoRegisterRequest request) {
        Member member = jwtProvider.getMemberByRawToken(token);

        // 기본 회원정보 없는 사람들 차단
        memberValidator.check_info(member);

        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(member) // 요청회원정보
                .orderNumber(orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE)) // 주문번호 생성
                .expireDate(LocalDateTime.now().plusDays(7)) // 무통장입금의 경우 입금대기기간 1주일로 임의로 잡았음
                .subscribeState(SubscribeState.APPROVED) // 상태는 요청 //FIXME : 테스트용으로 APPROVED로 설정함
                .subscribeType(request.getSubscribeType()) // 구독타입
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        // Slack에 알림 보내기
        slackNotifier.sendSubscribeSlackNotification(slackMapper.toSubscribeSlackNoticeString(member, request, subscriberInfo));

        return subscriberInfoMapper.toSubscriberInfoResponse(subscriberInfo, member);
    }
}
