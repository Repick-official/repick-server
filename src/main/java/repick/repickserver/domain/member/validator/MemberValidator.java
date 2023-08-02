package repick.repickserver.domain.member.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.global.error.exception.CustomException;

import java.util.Objects;
import java.util.Optional;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Service @RequiredArgsConstructor
public class MemberValidator {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void check_info(Member member) {
        if (member.getNickname() == null
                || member.getPhoneNumber() == null
                || member.getAddress() == null
                || member.getBank() == null
                || member.getName() == null
                || member.getEmail() == null)
            throw new CustomException(ACCESS_DENIED_NO_USER_INFO);
    }

    public void validateDuplicateEmail(String email, String userId) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        byEmail.ifPresent(m -> {
            if (!Objects.equals(m.getUserId(), userId))
                throw new CustomException(EMAIL_ALREADY_EXISTS);
        });
    }

    public void validateDuplicateEmail(String email) {
        if (memberRepository.existsByEmail(email))
            throw new CustomException(EMAIL_ALREADY_EXISTS);
    }

    public void validatePassword(Member member, String password) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException("비밀번호가 올바르지 않습니다.", MEMBER_NOT_FOUND);
        }

    }

    public void validateDuplicateNickname(String nickname, String userId) {
        Optional<Member> byNickname = memberRepository.findByNickname(nickname);
        byNickname.ifPresent(m -> {
            if (!Objects.equals(m.getUserId(), userId))
                throw new CustomException(NICKNAME_ALREADY_EXISTS);
        });
    }


    public void validateDuplicateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname))
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
    }
}
