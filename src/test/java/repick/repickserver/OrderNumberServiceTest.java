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

    @DisplayName("상품번호 생성 테스트")
    @Test
    void createProductNumberTest() {
        // given
        String productNumber = orderNumberService.generateProductNumber();
        String productNumber1 = orderNumberService.generateProductNumber();
        String productNumber2 = orderNumberService.generateProductNumber();
        String productNumber3 = orderNumberService.generateProductNumber();
        String productNumber4 = orderNumberService.generateProductNumber();
        String productNumber5 = orderNumberService.generateProductNumber();
        String productNumber6 = orderNumberService.generateProductNumber();
        String productNumber7 = orderNumberService.generateProductNumber();
        String productNumber8 = orderNumberService.generateProductNumber();
        String productNumber9 = orderNumberService.generateProductNumber();

        // when
        System.out.println("productNumber = " + productNumber);
        System.out.println("productNumber1 = " + productNumber1);
        System.out.println("productNumber2 = " + productNumber2);
        System.out.println("productNumber3 = " + productNumber3);
        System.out.println("productNumber4 = " + productNumber4);
        System.out.println("productNumber5 = " + productNumber5);
        System.out.println("productNumber6 = " + productNumber6);
        System.out.println("productNumber7 = " + productNumber7);
        System.out.println("productNumber8 = " + productNumber8);
        System.out.println("productNumber9 = " + productNumber9);

        // then
    }


}
