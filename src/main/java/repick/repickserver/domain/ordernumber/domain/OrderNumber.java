package repick.repickserver.domain.ordernumber.domain;

import lombok.*;
import repick.repickserver.domain.model.BaseTimeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Entity @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderNumber extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String orderNumber;

    /*
    * 주문 번호인데, 굳이 외래키로 연결하진 않겠음.
     */
}
