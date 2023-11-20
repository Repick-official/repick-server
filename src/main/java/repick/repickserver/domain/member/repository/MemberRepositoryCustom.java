package repick.repickserver.domain.member.repository;

import repick.repickserver.domain.member.dto.SignUserInfoResponse;

import java.util.List;

public interface MemberRepositoryCustom {
    List<SignUserInfoResponse> findPage(Long cursorId, int pageSize);
}
