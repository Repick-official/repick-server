package repick.repickserver.domain.member.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import repick.repickserver.domain.member.domain.Member;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(@Param("email") String email);
}