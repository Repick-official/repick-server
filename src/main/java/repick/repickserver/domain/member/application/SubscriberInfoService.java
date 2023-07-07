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
            subscriberInfo.getMember().equals(member) &&
            subscriberInfo.getExpireDate().isAfter(LocalDateTime.now()) &&
            subscriberInfo.getSubscribeState().equals(SubscribeState.APPROVED)
            );

    }

    public List<SubscriberInfoResponse> history(String token) throws Exception {
        Member member = jwtProvider.getMemberByRawToken(token);
        List<SubscriberInfoResponse> subscriberInfoResponses = new ArrayList<>();

        List<SubscriberInfo> subscriberInfoRepositoryAll = subscriberInfoRepository.findAll();

        Stream<SubscriberInfo> subscriberInfoStream = subscriberInfoRepositoryAll
                .stream().filter(subscriberInfo -> subscriberInfo.getMember().equals(member));

        subscriberInfoStream.forEach(subscriberInfo -> {
            subscriberInfoResponses.add(SubscriberInfoResponse.builder()
                    .createdAt(subscriberInfo.getCreatedAt())
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
                .expireDate(request.getExpireDate())
                .subscribeState(SubscribeState.APPROVED)
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        return true;

    }

    public Boolean deny(SubscriberInfoRequest request) throws Exception {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(member)
                .expireDate(request.getExpireDate())
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
