package repick.repickserver.domain.ordernumber.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.ordernumber.domain.OrderType;
import repick.repickserver.domain.ordernumber.dao.OrderNumberReository;
import repick.repickserver.domain.ordernumber.domain.OrderNumber;
import repick.repickserver.global.error.exception.CustomException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static repick.repickserver.global.error.exception.ErrorCode.INVALID_ORDER_TYPE;

@Service
@RequiredArgsConstructor
public class OrderNumberService {

    private final OrderNumberReository orderNumberReository;

    /*
     * 햇갈릴 수 있는 문자 제외 : 숫자 2, 5 영문자 E, I, L, O 제외 -> 총 30개
     * 왜 제외했나요 :
     *          2 : E와 발음이 동일
     *          5 : O와 발음이 동일
     *          E : 2와 발음이 동일
     *          I : L과 모양이 비슷
     *          L : I와 모양이 비슷
     *          O : 5와 발음이 동일
     */
    private static final String CHARACTERS = "01346789ABCDFGHJKMNPQRSTUVWXYZ";
    private static final int LENGTH = 5;

    private String saveOrderNumber(StringBuilder sb) {

        /*
         * 5자리 정수 생성 후, 해당 주문번호가 이미 존재하는지 확인을 반복한다.
         * 매일, 타입마다 약 30^5 개의 주문번호를 생성할 수 있다. ( 정확히는 30^5 - 30^4 = 23,490,000개 )
         * 현재 추정되는 하루 주문건수가 매우 낮으므로 서버에 치명적 문제가 발생할 확률이 매우 매우 매우 낮다.
         * (하루에 2천만개 거래되면 난 이미 부자일 것이므로 서버고 뭐고 ...헉)
         */
        String randomString = "";
        do {
            randomString = generateRandomString();
        } while (orderNumberReository.existsByOrderNumber(sb + randomString));

        OrderNumber orderNumber = OrderNumber.builder()
                .orderNumber(sb + randomString).build();

        // 주문번호 저장
        orderNumberReository.save(orderNumber);
        return orderNumber.getOrderNumber();
    }

    private String generateDateString() {
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        return localDate.format(formatter);
    }

    private String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public String generateOrderNumber(OrderType orderType) {

        /*
        * 주문 번호 생성 로직
        * 포맷은 TYYMMDDXXXXXX 이다.
        * T : Type (S : Subscribe, O : Order, R : SellOrder)
        * YY : 년도
        * MM : 월
        * DD : 일
        * XXXXX : 5자리 영문자 + 숫자
         */

        // 코드 타입 변환
        StringBuilder sb = new StringBuilder();
        switch (orderType) {
            case SUBSCRIBE:
                sb.append("S");
                break;
            case ORDER:
                sb.append("O");
                break;
            case SELL_ORDER:
                sb.append("R");
                break;
            case HOME_FITTING:
                sb.append("H");
                break;
            default:
                throw new CustomException("오더 타입이 올바르지 않습니다.", INVALID_ORDER_TYPE);
        }

        // 년도와 날짜 정보를 담는다: yyMMdd 형식
        sb.append(generateDateString());
        return saveOrderNumber(sb);
    }

    public String generateProductNumber() {

        /*
         * 주문 번호 생성 로직
         * 포맷은 TCCYYMMDDXXXXXX 이다.
         * T : Type ( 항상 'P' )
         * CC : 카테고리 ID
         * YY : 년도
         * MM : 월
         * DD : 일
         * XXXXX : 5자리 영문자 + 숫자
         */
        StringBuilder sb = new StringBuilder();
        sb.append("P");

        // 년도와 날짜 정보를 담는다: yyMMdd 형식
        sb.append(generateDateString());
        return saveOrderNumber(sb);

    }


}
