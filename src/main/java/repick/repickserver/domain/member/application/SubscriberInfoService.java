package repick.repickserver.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.SubscriberInfoRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.member.domain.SubscriberInfo;
import repick.repickserver.domain.member.dto.SubscriberInfoResponse;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriberInfoService {

    private final SubscriberInfoRepository subscriberInfoRepository;

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
                    .expireDate(subscriberInfo.getCreatedAt().plusMonths(1))
                    .build());
        });

        return subscriberInfoResponses;

    }
}
