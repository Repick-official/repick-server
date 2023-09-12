package repick.repickserver.domain.subscriberinfo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeType;

@Getter
public class SubscriberInfoRegisterRequest {

    @Schema(description = "플랜 이름", example = "BASIC")
    private SubscribeType subscribeType;
}
