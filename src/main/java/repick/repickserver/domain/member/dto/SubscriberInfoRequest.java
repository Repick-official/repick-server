package repick.repickserver.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscriberInfoRequest {
    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "유저식별용 id", example = "1")
    private String userId;

}
