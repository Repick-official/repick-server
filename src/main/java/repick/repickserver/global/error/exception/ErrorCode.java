package repick.repickserver.global.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    AUTHENTICATION_REQUIRED(401, "C001", "인증이 필요합니다."),
    INVALID_AUTHORIZATION_CODE(401, "C002", "유효하지 않은 인증 코드입니다."),
    ACCESS_DENIED(403, "C002", "권한이 없는 사용자입니다."),
    ACCESS_DENIED_NOT_SUBSCRIBED(403, "C003", "구독하지 않은 사용자입니다."),
    ACCESS_DENIED_NO_USER_INFO(403, "C003", "사용자 정보가 없습니다."),
    ENTITY_NOT_FOUND(400, "C003", "존재하지 않는 객체입니다."),
    INTERNAL_SERVER_ERROR(500, "C004", "서버 에러입니다."),
    INVALID_REQUEST_ERROR(400, "C001", "잘못된 요청입니다."),
    TOKEN_MEMBER_NO_MATCH(400, "M001", "토큰과 일치하는 회원이 없습니다."),
    TOKEN_EXPIRED(400, "M002", "토큰이 만료되었습니다."),
    INVALID_INPUT_VALUE(400, "C005", "잘못된 입력값입니다."),

    // Member
    MEMBER_NOT_FOUND(400, "M001", "존재하지 않는 회원입니다."),
    MEMBER_REGISTER_FAIL(400, "M002", "회원 생성에 실패했습니다."),
    SUBSCRIBER_INFO_NOT_FOUND(400, "M003", "존재하지 않는 구독 정보입니다."),
    MEMBER_UPDATE_FAIL(400, "M004", "회원 정보 수정에 실패했습니다."),
    EMAIL_NOT_FOUND(400, "M005", "존재하지 않는 이메일입니다."),
    EMAIL_ALREADY_EXISTS(400, "M005", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(400, "M006", "이미 존재하는 닉네임입니다."),

    // Order
    ORDER_FAIL(400, "O001", "주문에 실패했습니다."),
    ORDER_NOT_FOUND(400, "O001", "존재하지 않는 주문입니다."),
    PATH_NOT_RESOLVED(400, "O002", "경로를 찾을 수 없습니다."),
    ORDER_STATE_NOT_FOUND(400, "O003", "요청한 주문 상태가 존재하지 않습니다."),
    ORDER_MEMBER_NOT_MATCH(400, "O004", "주문과 회원이 일치하지 않습니다."),
    SMS_SEND_FAILED(500, "O005", "주문 내역 SMS 발송에 실패했습니다."),
    ORDER_NAME_NOT_FOUND(400, "O006", "주문자 이름을 입력해주세요."),
    ORDER_ADDRESS_NOT_FOUND(400, "O007", "주문자 주소를 입력해주세요."),
    ORDER_BAG_QUANTITY_NOT_FOUND(400, "O008", "주문자 주문 가방 수량을 입력해주세요."),
    ORDER_PRODUCT_QUANTITY_NOT_FOUND(400, "O009", "주문자 주문 상품 수량을 입력해주세요."),
    ORDER_PHONE_NUMBER_NOT_FOUND(400, "O010", "주문자 전화번호를 입력해주세요."),

    // Product
    REQUEST_BODY_INVALID(400, "P001", "요청 바디가 잘못되었습니다."),
    IMAGE_FILE_MISSING(400, "P001", "상품의 사진 파일을 입력해주세요."),
    S3_UPLOAD_FAIL(500, "P002", "S3 업로드에 실패했습니다."),
    S3_FILE_CONVERT_FAIL(500, "P003", "S3 업로드를 위한 파일 변환에 실패했습니다."),
    EMPTY_PRODUCT_NAME(400, "P004", "상품의 이름을 입력해주세요."),
    EMPTY_PRODUCT_PRICE(400, "P005", "상품의 가격을 입력해주세요."),
    PRODUCT_MAIN_IMAGE_NOT_FOUND(500, "P006", "상품의 메인 이미지를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(400, "P006", "존재하지 않는 상품입니다."),
    PRODUCT_IMAGE_NOT_FOUND(400, "P007", "존재하지 않는 상품 이미지입니다."),
    INVALID_CATEGORY_ID(400, "P008", "존재하지 않는 카테고리입니다."),
    PRODUCT_SOLD_OUT(400, "P009", "품절된 상품입니다."),
    PRODUCT_NOT_SOLD_OUT(400, "P010", "판매되지 않은 상품입니다."),
    PRODUCT_NOT_PENDING(400, "P009", "주문 신청이 없는 상품입니다."),
    PRODUCT_NOT_SELLING(400, "P010", "품절 되었거나 판매 중이 아닌 상품입니다."),
    KEYWORD_PRODUCT_NOT_FOUND(400, "P011", "검색 결과가 없습니다."),
    INVALID_PRICE_SORT_TYPE(400, "P012", "정렬 타입은 high 또는 low 로 요청해주세요."),

    // MyPick, HomeFitting
    INVALID_CART_PRODUCT_ID(400, "C001", "존재하지 않는 마이픽 상품입니다."),
    INVALID_CART_PRODUCT_STATE(400, "C002", "홈피팅 신청할 수 없는 마이픽 상품입니다."),
    INVALID_HOME_FITTING_ID(400, "C004", "존재하지 않는 홈피팅입니다."),
    PRODUCT_ALREADY_EXIST_IN_CART(400, "C005", "이미 장바구니에 담긴 상품입니다."),

    //OrderNumber
    INVALID_ORDER_TYPE(400, "C005", "잘못된 오더 타입입니다."),
    ORDER_NUMBER_NOT_FOUND(400, "C006", "존재하지 않는 주문 번호입니다."),
    PRODUCT_NOT_SETTLEMENT_REQUESTED(400, "C007", "결제 요청되지 않은 상품입니다."),
    //Delivery
    WAYBILL_NUMBER_NOT_REGISTERED(400, "C006", "운송장 번호가 등록되지 않았습니다."),
    //PayApp
    LINK_NOT_FOUND(400, "C007", "존재하지 않는 링크입니다."),
    PRICE_INVALID(400, "C008", "가격이 유효하지 않습니다.");


    private final int status;
    private final String code;
    private final String message;

}
