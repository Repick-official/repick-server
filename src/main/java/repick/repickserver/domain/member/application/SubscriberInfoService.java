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

    public Boolean add(SubscriberInfoRequest request) throws Exception {
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
