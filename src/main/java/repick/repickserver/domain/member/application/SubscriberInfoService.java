package repick.repickserver.domain.member.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.dao.SubscriberInfoRepository;
import repick.repickserver.domain.member.domain.Member;
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


    public Boolean check(Member member) {
        List<SubscriberInfo> subscriberInfoRepositoryAll = subscriberInfoRepository.findAll();

        return subscriberInfoRepositoryAll.stream()
                .anyMatch(subscriberInfo ->
            subscriberInfo.getMember().equals(member) &&
            subscriberInfo.getExpireDate().isAfter(LocalDateTime.now())
            );

    }

    public List<SubscriberInfoResponse> history(Member member) {
        List<SubscriberInfoResponse> subscriberInfoResponses = new ArrayList<>();

        List<SubscriberInfo> subscriberInfoRepositoryAll = subscriberInfoRepository.findAll();

        Stream<SubscriberInfo> subscriberInfoStream = subscriberInfoRepositoryAll
                .stream().filter(subscriberInfo -> subscriberInfo.getMember().equals(member));

        subscriberInfoStream.forEach(subscriberInfo -> {
            subscriberInfoResponses.add(SubscriberInfoResponse.builder()
                    .createdAt(subscriberInfo.getCreatedAt())
                    .expireDate(subscriberInfo.getExpireDate())
                    .build());
        });

        return subscriberInfoResponses;

    }

    public Boolean add(SubscriberInfoRequest request) throws Exception {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));
        SubscriberInfo subscriberInfo = SubscriberInfo.builder()
                .member(member)
                .expireDate(request.getExpireDate())
                .build();

        subscriberInfoRepository.save(subscriberInfo);

        return true;

    }
}
