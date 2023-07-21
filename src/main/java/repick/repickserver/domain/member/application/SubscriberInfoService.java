package repick.repickserver.domain.member.application;

import com.mysema.commons.lang.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.SubscriberInfoRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;
import repick.repickserver.domain.member.dto.SubscriberInfoRegisterRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.global.Parser;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;
import repick.repickserver.infra.SlackNotifier;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriberInfoService {

    private final SubscriberInfoRepository subscriberInfoRepository;
    private final JwtProvider jwtProvider;
    private final OrderNumberService orderNumberService;
    private final SlackNotifier slackNotifier;


    /**
     * 토큰으로 요청한 사용자가 현재 구독중인지 판별한다.
     * @param token 토큰으로 사용자를 찾음
     * @return 구독중이면 true, 아니면 false
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @author seochanhyeok
     */
    public String check(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        SubscriberInfo validSubscriberInfo = subscriberInfoRepository.findValidSubscriberInfo(member.getId());

        if (validSubscriberInfo == null) {
            return "NONE";
        }
        System.out.println("validSubscriberInfo = " + validSubscriberInfo);
        return validSubscriberInfo.getSubscribeType().toString();

    }

    /**
     * 요청한 사용자의 구독 기록을 모두 반환한다.
     * @param token 토큰으로 사용자를 찾음
     * @param state 요청, 승인, 거절 상태를 구분
     * @return List<SubscriberInfoResponse> 구독 기록 리스트
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @author seochanhyeok
     */
    public List<SubscriberInfoResponse> history(String state, String token) {

        Pair<SubscribeState, Boolean> subscribeState = Parser.parseSubscribeState(state);

        Member member = jwtProvider.getMemberByRawToken(token);

        List<SubscriberInfo> subscriberInfos = subscriberInfoRepository.findSubscriberInfo(
                                                                        member.getId(),
                                                                        subscribeState.getFirst(),
                                                                        subscribeState.getSecond());

        List<SubscriberInfoResponse> subscriberInfoResponses = new ArrayList<>();
        subscriberInfos.forEach(subscriberInfo -> subscriberInfoResponses.add(SubscriberInfoResponse.builder()
                .id(subscriberInfo.getId())
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .build()));
        return subscriberInfoResponses;
    }

    /**
     * 모든 사용자의 처리되지 않은, 만료되지 않은 구독 요청들을 반환한다.
     * @return List<SubscriberInfoResponse> 구독 요청 리스트
     * @author seochanhyeok
     */
    public List<SubscriberInfoResponse> getRequestedSubscriberInfos() {
        List<SubscriberInfoResponse> subscriberInfoResponses = new ArrayList<>();
        List<SubscriberInfo> subscriberInfos = subscriberInfoRepository.findValidRequests();
        subscriberInfos.forEach(subscriberInfo -> subscriberInfoResponses.add(SubscriberInfoResponse.builder()
                .id(subscriberInfo.getId())
                .email(subscriberInfo.getMember().getEmail())
                .name(subscriberInfo.getMember().getName())
                .nickname(subscriberInfo.getMember().getNickname())
                .phoneNumber(subscriberInfo.getMember().getPhoneNumber())
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .build()));
        return subscriberInfoResponses;
    }

    /**
     * <h1>관리자가 사용자의 구독을 승인한다: APPROVED 상태의 subscriberInfo 추가</h1>
     * @param request request.email 사용, 사용자의 이메일로 사용자를 찾음
     * @return SubscriberInfoResponse 승인된 구독 정보를 반환한다. (id, email, name, nickname, phoneNumber, orderNumber, createdDate, expireDate, subscribeState, subscribeType)
     * @author seochanhyeok
     */
    public SubscriberInfoResponse add(SubscriberInfoRequest request) {
        SubscriberInfo parent = subscriberInfoRepository.findByOrderNumberAndSubscribeState(request.getOrderNumber(), SubscribeState.REQUESTED);

        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(parent.getMember())
                .orderNumber(parent.getOrderNumber())
                .parentSubscriberInfo(parent)
                // 승인 시점으로부터 한 달 뒤 만료
                .expireDate(LocalDateTime.now().plusMonths(1))
                .subscribeState(SubscribeState.APPROVED)
                .subscribeType(parent.getSubscribeType())
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        return SubscriberInfoResponse.builder()
                .id(subscriberInfo.getId())
                .email(subscriberInfo.getMember().getEmail())
                .name(subscriberInfo.getMember().getName())
                .nickname(subscriberInfo.getMember().getNickname())
                .phoneNumber(subscriberInfo.getMember().getPhoneNumber())
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .build();

    }

    /**
     * <h1>관리자가 사용자의 구독을 거절한다: DENIED 상태의 subscriberInfo 추가</h1>
     * @param request request.email 사용, 사용자의 이메일로 사용자를 찾음
     * @return SubscriberInfoResponse 거절된 구독 정보를 반환한다. (id, email, name, nickname, phoneNumber, orderNumber, createdDate, expireDate, subscribeState, subscribeType)
     * @author seochanhyeok
     */
    public SubscriberInfoResponse deny(SubscriberInfoRequest request) {
        SubscriberInfo parent = subscriberInfoRepository.findByOrderNumberAndSubscribeState(request.getOrderNumber(), SubscribeState.REQUESTED);

        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(parent.getMember())
                .orderNumber(parent.getOrderNumber())
                .parentSubscriberInfo(parent)
                .expireDate(LocalDateTime.now()) // 만료시점을 현재로 설정하여 만료시킴
                .subscribeState(SubscribeState.DENIED)
                .subscribeType(parent.getSubscribeType())
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        return SubscriberInfoResponse.builder()
                .id(subscriberInfo.getId())
                .email(subscriberInfo.getMember().getEmail())
                .name(subscriberInfo.getMember().getName())
                .nickname(subscriberInfo.getMember().getNickname())
                .phoneNumber(subscriberInfo.getMember().getPhoneNumber())
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .build();

    }

    /**
     * 사용자가 구독을 요청한다: REQUESTED 상태의 subscriberInfo 추가
     * @param token 토큰으로 사용자를 찾음
     * @return SubscriberInfoResponse 구독 요청 정보
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @author seochanhyeok
     */
    public SubscriberInfoResponse subscribeRequest(String token, SubscriberInfoRegisterRequest request) {
        Member member = jwtProvider.getMemberByRawToken(token);

        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(member) // 요청회원정보
                .orderNumber(orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE)) // 주문번호 생성
                .expireDate(LocalDateTime.now().plusDays(7)) // 무통장입금의 경우 입금대기기간 1주일로 임의로 잡았음
                .subscribeState(SubscribeState.REQUESTED) // 상태는 요청
                .subscribeType(request.getSubscribeType()) // 구독타입
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        // Slack에 알림 보내기
        slackNotifier.sendSubscribeSlackNotification("구독 신청이 들어왔습니다." +
                "\n이름: " + member.getName() +
                "\n이메일: " + member.getEmail() +
                "\n구독타입: " + request.getSubscribeType() +
                "\n주문번호: " + subscriberInfo.getOrderNumber());

        return SubscriberInfoResponse.builder()
                .id(subscriberInfo.getId())
                .orderNumber(subscriberInfo.getOrderNumber())
                .createdDate(subscriberInfo.getCreatedDate())
                .expireDate(subscriberInfo.getExpireDate())
                .subscribeState(subscriberInfo.getSubscribeState())
                .subscribeType(subscriberInfo.getSubscribeType())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }

}
