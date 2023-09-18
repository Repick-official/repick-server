package repick.repickserver.domain.sellorder.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import repick.repickserver.domain.model.Address;
import repick.repickserver.domain.model.Bank;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.domain.SellState;

import javax.persistence.Embedded;
import java.time.LocalDateTime;

@Getter
@Builder
public class SellOrderResponse {

    @Schema(description = "DB 식별용 아이디", example = "1")
    private Long id;
    @Schema(description = "주문번호", example = "R2302188GHE1")
    private String orderNumber;
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
    @Schema(description = "수거 희망일자", example = "2021-08-31T00:00:00")
    private LocalDateTime returnDate;
    @Schema(description = "주문상태", example = "DELIVERED")
    private SellState sellState;
    @Schema(description = "생성일", example = "2021-08-31T00:00:00")
    private LocalDateTime createdDate;

    public static SellOrderResponse from(SellOrder sellOrder) {

        if (sellOrder.getSellOrderStates() == null || sellOrder.getSellOrderStates().isEmpty()) {
            return SellOrderResponse.builder()
                    .id(sellOrder.getId())
                    .name(sellOrder.getName())
                    .orderNumber(sellOrder.getOrderNumber())
                    .phoneNumber(sellOrder.getPhoneNumber())
                    .bank(sellOrder.getBank())
                    .bagQuantity(sellOrder.getBagQuantity())
                    .productQuantity(sellOrder.getProductQuantity())
                    .address(sellOrder.getAddress())
                    .requestDetail(sellOrder.getRequestDetail())
                    .returnDate(sellOrder.getReturnDate())
                    .createdDate(sellOrder.getCreatedDate())
                    .build();
        }

        return SellOrderResponse.builder()
                .id(sellOrder.getId())
                .name(sellOrder.getName())
                .orderNumber(sellOrder.getOrderNumber())
                .phoneNumber(sellOrder.getPhoneNumber())
                .bank(sellOrder.getBank())
                .bagQuantity(sellOrder.getBagQuantity())
                .productQuantity(sellOrder.getProductQuantity())
                .address(sellOrder.getAddress())
                .requestDetail(sellOrder.getRequestDetail())
                .returnDate(sellOrder.getReturnDate())
                // sellOrderStates 중 가장 최근에 업데이트된 state 가져옴
                .sellState(sellOrder.getSellOrderStates().get(sellOrder.getSellOrderStates().size() - 1).getSellState())
                .createdDate(sellOrder.getCreatedDate())
                .build();
    }

}
