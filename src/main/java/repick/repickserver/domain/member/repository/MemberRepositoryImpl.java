package repick.repickserver.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import repick.repickserver.domain.member.dto.QSignUserInfoResponse;
import repick.repickserver.domain.member.dto.SignUserInfoResponse;

import java.util.List;

import static repick.repickserver.domain.member.domain.QMember.member;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SignUserInfoResponse> findPage(Long cursorId, int pageSize) {
        return jpaQueryFactory
                .select(new QSignUserInfoResponse(member))
                .from(member)
                .where(ltMemberId(cursorId))
                .orderBy(member.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression ltMemberId(Long cursorId) { // 첫 페이지 조회와 두번째 이상 페이지 조회를 구분하기 위함
        return cursorId != null ? member.id.lt(cursorId) : null;
    }
}
