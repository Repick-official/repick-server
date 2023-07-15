package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.member.domain.SubscribeType;

@Getter @Builder @AllArgsConstructor @NoArgsConstructor
public class SubscriberInfoRegisterRequest {

    @Schema(description = "플랜 이름", example = "BASIC")
    private SubscribeType subscribeType;
}
