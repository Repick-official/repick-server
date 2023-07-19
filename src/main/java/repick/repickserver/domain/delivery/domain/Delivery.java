package repick.repickserver.domain.delivery.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @NoArgsConstructor
public class Delivery extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    /*
     * 택배사 코드
     * 스마트 택배 API 호출 용도
     * 04 : CJ 대한통운
     * 08 : 롯데택배
     * 46 : CU 편의점택배
     */
    private String  code;

    // 운송장 번호
    private String waybillNumber;

    // 주문 번호
    private String orderNumber;

    @Builder
    public Delivery(String code, String waybillNumber, String orderNumber) {
        this.code = code;
        this.waybillNumber = waybillNumber;
        this.orderNumber = orderNumber;
    }

}
