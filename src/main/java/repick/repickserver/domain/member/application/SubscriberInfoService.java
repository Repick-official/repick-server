package repick.repickserver.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.dao.SubscriberInfoRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.SubscribeState;
import repick.repickserver.domain.member.domain.SubscriberInfo;
import repick.repickserver.domain.member.dto.SubscriberInfoRequest;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;
import repick.repickserver.global.jwt.JwtProvider;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SubscriberInfoService {

    private final SubscriberInfoRepository subscriberInfoRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;


    /**
     * 토큰으로 요청한 사용자가 현재 구독중인지 판별한다.
     * @param token 토큰으로 사용자를 찾음
     * @return 구독중이면 true, 아니면 false
     * @author seochanhyeok
     */
    public Boolean check(String token) throws Exception {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<SubscriberInfo> subscriberInfoRepositoryAll = subscriberInfoRepository.findAll();

        return subscriberInfoRepositoryAll.stream()
                .anyMatch(subscriberInfo ->
                // member 가 일치
                subscriberInfo.getMember().equals(member) &&
                // 승인됨
                subscriberInfo.getSubscribeState().equals(SubscribeState.APPROVED) &&
                // expireDate 가 null 인 경우(deny 됨) 제외 (사실 필요없으나 assert 하는것임)
                subscriberInfo.getExpireDate() != null &&
                // 만료되지 않음
                subscriberInfo.getExpireDate().isAfter(LocalDateTime.now())
        );

    }

    /**
     * 요청한 사용자의 구독 기록을 모두 반환한다.
     * @param token 토큰으로 사용자를 찾음
     * @return List<SubscriberInfoResponse> 구독 기록 리스트
     * @author seochanhyeok
     */
    public List<SubscriberInfoResponse> history(String token) throws Exception {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<SubscriberInfoResponse> subscriberInfoResponses = new ArrayList<>();

        // 모든 subscriberInfo 중 멤버가 일치하는것만 필터링
        Stream<SubscriberInfo> subscriberInfoStream = subscriberInfoRepository.findAll()
                .stream().filter(subscriberInfo -> subscriberInfo.getMember().equals(member));


        subscriberInfoStream.forEach(subscriberInfo -> {
            // 각각을 반환 dto 에 담음
            subscriberInfoResponses.add(SubscriberInfoResponse.builder()
                    .createdDate(subscriberInfo.getCreatedDate())
                    .expireDate(subscriberInfo.getExpireDate())
                    .subscribeState(subscriberInfo.getSubscribeState())
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
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(member)
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
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(member)
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
     * @author seochanhyeok
     */
    public Boolean subscribeRequest(String token) throws Exception {
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
