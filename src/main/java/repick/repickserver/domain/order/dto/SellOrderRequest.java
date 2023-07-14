package repick.repickserver.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.order.domain.SellState;

import javax.persistence.Embedded;
import java.time.LocalDateTime;

@Getter
public class SellOrderRequest {

    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "주문자 성함", example = "이리픽")
    private String name;
    @Schema(description = "주문자 전화번호", example = "01012345678")
    private String phoneNumber;
    @Schema(description = "주문자 은행계좌", example = "카카오뱅크")
    private String bankName;
    @Schema(description = "주문자 계좌번호", example = "3333980010619")
    private String accountNumber;
    @Schema(description = "리픽백 수량", example = "3")
    private Integer bagQuantity;
    @Schema(description = "의류 수량", example = "7")
    private Integer productQuantity;
    @Embedded
    private Address address;
    @Schema(description = "수거시 추가 요청사항", example = "사랑을 가득 담아주세요~")
    private String requestDetail;
    @Schema(description = "수거 희망일자", example = "2021-08-31T00:00:00")
    private LocalDateTime returnDate;
    @Schema(description = "주문상태", example = "DELIVERED")
    private SellState sellState;

}
