package repick.repickserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;
import repick.repickserver.domain.ordernumber.domain.OrderType;

@SpringBootTest
public class OrderNumberServiceTest {

    @Autowired
    OrderNumberService orderNumberService;


    /**
     * 주문번호 생성 테스트
     * 테스트 주의사항 : 로컬 DB 사용할것
     */
    @DisplayName("주문번호 생성 테스트")
    @Test
    void createOrderNumberTest() {
        // given
        String orderNumber = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber1 = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber2 = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber3 = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber4 = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber5 = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber6 = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber7 = orderNumberService.generateOrderNumber(OrderType.SUBSCRIBE);
        String orderNumber8 = orderNumberService.generateOrderNumber(OrderType.ORDER);
        String orderNumber9 = orderNumberService.generateOrderNumber(OrderType.SELL_ORDER);

        // when
        System.out.println("orderNumber = " + orderNumber);
        System.out.println("orderNumber1 = " + orderNumber1);
        System.out.println("orderNumber2 = " + orderNumber2);
        System.out.println("orderNumber3 = " + orderNumber3);
        System.out.println("orderNumber4 = " + orderNumber4);
        System.out.println("orderNumber5 = " + orderNumber5);
        System.out.println("orderNumber6 = " + orderNumber6);
        System.out.println("orderNumber7 = " + orderNumber7);
        System.out.println("orderNumber8 = " + orderNumber8);
        System.out.println("orderNumber9 = " + orderNumber9);

        // then
    }


}
