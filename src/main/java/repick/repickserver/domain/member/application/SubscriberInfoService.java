package repick.repickserver.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.SubscriberInfoRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.global.error.exception.CustomException;
import repick.repickserver.global.jwt.JwtProvider;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriberInfoService {

    private final SubscriberInfoRepository subscriberInfoRepository;
    private final JwtProvider jwtProvider;
    private final OrderNumberService orderNumberService;


    /**
     * 토큰으로 요청한 사용자가 현재 구독중인지 판별한다.
     * @param token 토큰으로 사용자를 찾음
     * @return 구독중이면 true, 아니면 false
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @author seochanhyeok
     */
    public Boolean check(String token) {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<SubscriberInfo> validSubscriberInfo = subscriberInfoRepository.findValidSubscriberInfo(member.getId());
        return !validSubscriberInfo.isEmpty();
    }

    /**
     * 요청한 사용자의 구독 기록을 모두 반환한다.
     * @param token 토큰으로 사용자를 찾음
     * @return List<SubscriberInfoResponse> 구독 기록 리스트
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @author seochanhyeok
     */
    public List<SubscriberInfoResponse> history(String state, String token) {
        SubscribeState subscribeState;
        boolean isExpired = false;
        switch (state) {
            case "requested":
                subscribeState = SubscribeState.REQUESTED;
                break;
            case "approved":
                subscribeState = SubscribeState.APPROVED;
                break;
            case "denied":
                subscribeState = SubscribeState.DENIED;
                break;
            case "request-expired":
                subscribeState = SubscribeState.REQUESTED;
                isExpired = true;
                break;
            case "expired":
                subscribeState = SubscribeState.APPROVED;
                isExpired = true;
                break;
            default:
                throw new CustomException("잘못된 주소 요청입니다.", PATH_NOT_RESOLVED);
        }

        Member member = jwtProvider.getMemberByRawToken(token);

        List<SubscriberInfo> subscriberInfos = subscriberInfoRepository.findSubscriberInfo(member.getId(), subscribeState, isExpired);
        List<SubscriberInfoResponse> subscriberInfoResponses = new ArrayList<>();
        subscriberInfos.forEach(subscriberInfo -> {
            subscriberInfoResponses.add(SubscriberInfoResponse.builder()
                    .createdDate(subscriberInfo.getCreatedDate())
                    .expireDate(subscriberInfo.getExpireDate())
                    .build());
        });
        return subscriberInfoResponses;
    }

    public List<SubscriberInfoResponse> getRequestedSubscriberInfos() {
        List<SubscriberInfoResponse> subscriberInfoResponses = new ArrayList<>();
        List<SubscriberInfo> subscriberInfos = subscriberInfoRepository.findValidRequests();
        subscriberInfos.forEach(subscriberInfo -> {
            subscriberInfoResponses.add(SubscriberInfoResponse.builder()
                    .id(subscriberInfo.getId())
                    .createdDate(subscriberInfo.getCreatedDate())
                    .expireDate(subscriberInfo.getExpireDate())
                    .build());
        });
        return subscriberInfoResponses;
    }

    /**
     * 관리자가 사용자의 구독을 승인한다: APPROVED 상태의 subscriberInfo 추가
     * @param request request.email 사용, 사용자의 이메일로 사용자를 찾음
     * @return true
     * @author seochanhyeok
     */
    public Boolean add(SubscriberInfoRequest request) {
//        Member member = memberRepository.findByUserId(request.getUserId())
//                .orElseThrow(() -> new CustomException("존재하지 않는 사용자입니다.", MEMBER_NOT_FOUND));
        SubscriberInfo parent = subscriberInfoRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException("존재하지 않는 구독 요청입니다.", SUBSCRIBER_INFO_NOT_FOUND));

        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(parent.getMember())
                .parentSubscriberInfo(parent)
                // 승인 시점으로부터 한 달 뒤 만료
                .expireDate(LocalDateTime.now().plusMonths(1))
                .subscribeState(SubscribeState.APPROVED)
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        return true;

    }

    /**
     * 관리자가 사용자의 구독을 거절한다: DENIED 상태의 subscriberInfo 추가
     * @param request request.email 사용, 사용자의 이메일로 사용자를 찾음
     * @return 구독중이면 true, 아니면 false
     * @author seochanhyeok
     */
    public Boolean deny(SubscriberInfoRequest request) {
        SubscriberInfo parent = subscriberInfoRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException("존재하지 않는 구독 요청입니다.", SUBSCRIBER_INFO_NOT_FOUND));

        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(parent.getMember())
                .parentSubscriberInfo(parent)
                // deny 된 경우 expireDate 는 null
                .subscribeState(SubscribeState.DENIED)
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        return true;

    }

    /**
     * 사용자가 구독을 요청한다: REQUESTED 상태의 subscriberInfo 추가
     * @param token 토큰으로 사용자를 찾음
     * @return false(결제로직 미정으로 false 처리해두었음)
     * @exception CustomException (TOKEN_MEMBER_NO_MATCH) 토큰에 해당하는 멤버의 userId를 찾을 수 없을 때
     * @author seochanhyeok
     */
    public Boolean subscribeRequest(String token) {
        // TODO: 결제로직 미정!!! 수정 필요
        Member member = jwtProvider.getMemberByRawToken(token);

        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(member)
                .expireDate(LocalDateTime.now().plusDays(7)) // 무통장입금의 경우 입금대기기간 1주일로 임의로 잡았음
                .subscribeState(SubscribeState.REQUESTED)
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        return false;
    }

}
