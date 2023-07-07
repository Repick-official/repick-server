package repick.repickserver.domain.product.domain;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private String detail;

    private Integer price;

    private String size;

    // TODO: Long / Integer 중 뭐하지
    private Long discountRate;


}
