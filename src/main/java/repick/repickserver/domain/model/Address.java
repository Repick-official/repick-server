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
public class Address {
    @Schema(description = "메인 주소", example = "서울시 강남구")
    public String mainAddress;
    @Schema(description = "상세 주소", example = "테헤란로 427")
    public String detailAddress;
    @Schema(description = "우편 번호", example = "12345")
    public String zipCode;

}
