package repick.repickserver.domain.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import repick.repickserver.domain.user.domain.Member;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}