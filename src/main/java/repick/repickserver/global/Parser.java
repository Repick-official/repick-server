package repick.repickserver.global;

import com.mysema.commons.lang.Pair;
import repick.repickserver.domain.sellorder.domain.SellState;
import repick.repickserver.domain.subscriberinfo.domain.SubscribeState;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.PATH_NOT_RESOLVED;

public class Parser {

    /**
     * <h1>String을 SellState으로 parse</h1>
     * @param state string (requested | canceled | delivered | published)
     * @exception CustomException (PATH_NOT_RESOLVED)
     * @return SellState
     * @author seochanhyeok
     */
    public static SellState parseSellState(String state) {
        // state 대문자로 변환
        state = state.toUpperCase();
        // state 에 따른 SellState로 parse
        try {
            SellState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new CustomException(PATH_NOT_RESOLVED);
        }
        return SellState.valueOf(state);
    }

    /**
     * <h1>String을 SubscribeState으로 parse</h1>
     * @param state string (requested | approved | denied | request-expired | expired)
     * @exception CustomException (PATH_NOT_RESOLVED)
     * @return Pair<SubscribeState, Boolean> (SubscribeState, isExpired)
     * @author seochanhyeok
     */
    public static Pair<SubscribeState, Boolean> parseSubscribeState(String state) {
        SubscribeState subscribeState;
        boolean isExpired = false;

        switch (state) {
            case "requested":
                subscribeState = SubscribeState.REQUESTED;
                break;
            case "approved":
                subscribeState = SubscribeState.APPROVED;
                break;
            case "denied":
                subscribeState = SubscribeState.DENIED;
                isExpired = true;
                break;
            case "request-expired":
                subscribeState = SubscribeState.REQUESTED;
                isExpired = true;
                break;
            case "expired":
                subscribeState = SubscribeState.APPROVED;
                isExpired = true;
                break;
            default:
                throw new CustomException("잘못된 주소 요청입니다.", PATH_NOT_RESOLVED);
        }
        return new Pair<>(subscribeState, isExpired);
    }

    public static String parseProductListToString(int quantity, String name) {

        return "[리픽]\n\n" +
                "안녕하세요, " + name + "님 :)\n" +
                "현재 " + quantity + "건의 상품 등록이 완료되었습니다.\n" +
                "상품 확인 후 상품 가격 입력을 완료해주시면 판매가 시작됩니다.\n" +
                "감사합니다 ♥";
    }

    public static String normalizeReturnDate(String returnDate) {
        // returnDate 예시 : 20230831 || 2023-08-31
        if (returnDate.length() == 8) {
            returnDate = returnDate.substring(0, 4) + "-" + returnDate.substring(4, 6) + "-" + returnDate.substring(6);
        }

        // 반환 예시 : 2023-08-31
        return returnDate;

    }
}
