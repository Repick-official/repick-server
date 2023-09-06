package repick.repickserver.domain.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import repick.repickserver.domain.delivery.domain.Delivery;

@Getter
public class DeliveryResponse {

    @Schema(description = "택배사 코드", example = "04")
    private String code;

    @Schema(description = "운송장 번호", example = "123456789")
    private String waybillNumber;

    @Schema(description = "주문 번호", example = "S2305021U8DA")
    private String orderNumber;

    @Builder
    private DeliveryResponse(String code, String waybillNumber, String orderNumber) {
        this.code = code;
        this.waybillNumber = waybillNumber;
        this.orderNumber = orderNumber;
    }

    public static DeliveryResponse from(Delivery delivery) {
        return DeliveryResponse.builder()
                .code(delivery.getCode())
                .waybillNumber(delivery.getWaybillNumber())
                .orderNumber(delivery.getOrderNumber())
                .build();
    }

}
