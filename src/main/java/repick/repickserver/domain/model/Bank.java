package repick.repickserver.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    @Schema(description = "은행계좌", example = "카카오뱅크")
    private String bankName;

    @Schema(description = "계좌번호", example = "3333980010619")
    private String accountNumber;
}
