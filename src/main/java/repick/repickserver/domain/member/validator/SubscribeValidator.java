package repick.repickserver.domain.member.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.SubscriberInfoRepository;
import repick.repickserver.global.jwt.JwtProvider;

@Service @RequiredArgsConstructor
public class SubscribeValidator {

    private final SubscriberInfoRepository subscriberInfoRepository;
    private final JwtProvider jwtProvider;

    public String check(String token) {
        return subscriberInfoRepository.findValidSubscriberInfo(jwtProvider.getMemberByRawToken(token).getId())
                // 유효한 구독 정보 있으면 반환
                .map(subscriberInfo -> subscriberInfo.getSubscribeType().toString())
                // 없으면 NONE 반환
                .orElse("NONE");
    }
}
