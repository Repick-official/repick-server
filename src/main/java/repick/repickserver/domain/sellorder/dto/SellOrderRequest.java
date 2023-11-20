package repick.repickserver.domain.sellorder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.model.Bank;
import repick.repickserver.domain.sellorder.domain.SellState;
import repick.repickserver.global.error.exception.CustomException;

import javax.persistence.Embedded;

import static repick.repickserver.global.error.exception.ErrorCode.*;

@Getter
public class SellOrderRequest {

    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "주문자 성함", example = "이리픽")
    private String name;
    @Schema(description = "주문자 전화번호", example = "01012345678")
    private String phoneNumber;
    @Schema(description = "리픽백 수량", example = "3")
    private Integer bagQuantity;
    @Schema(description = "의류 수량", example = "7")
    private Integer productQuantity;
    @Embedded
    private Bank bank;
    @Embedded
    private Address address;
    @Schema(description = "수거시 추가 요청사항", example = "사랑을 가득 담아주세요~")
    private String requestDetail;
    @Schema(description = "주문상태", example = "DELIVERED")
    private SellState sellState;

    public static void validateSellOrder(SellOrderRequest request) {
        if (request.getName() == null) throw new CustomException(ORDER_NAME_NOT_FOUND);
        if (request.getAddress() == null
                || request.getAddress().getMainAddress() == null
                || request.getAddress().getDetailAddress() == null
                || request.getAddress().getZipCode() == null) throw new CustomException(ORDER_ADDRESS_NOT_FOUND);
        if (request.getBagQuantity() == null) throw new CustomException(ORDER_BAG_QUANTITY_NOT_FOUND);
        if (request.getProductQuantity() == null) throw new CustomException(ORDER_PRODUCT_QUANTITY_NOT_FOUND);
        if (request.getPhoneNumber() == null) throw new CustomException(ORDER_PHONE_NUMBER_NOT_FOUND);
    }

}
