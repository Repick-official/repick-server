package repick.repickserver.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.repository.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.TOKEN_MEMBER_NO_MATCH;

@Service
@RequiredArgsConstructor
public class JpaUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        System.out.println("userId = " + userId);
        Member member = memberRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException("존재하지 않는 회원입니다.", TOKEN_MEMBER_NO_MATCH)
        );

        return new UserDetailsImpl(member);
    }
}