package repick.repickserver.global.error.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    ACCESS_DENIED(403, "C002", "권한이 없는 사용자입니다."),
    ENTITY_NOT_FOUND(400, "C003", "존재하지 않는 객체입니다."),
    INTERNAL_SERVER_ERROR(500, "C004", "서버 에러입니다."),
    INVALID_REQUEST_ERROR(400, "C001", "잘못된 요청입니다."),

    // Member
    TOKEN_MEMBER_NO_MATCH(400, "M001", "토큰과 일치하는 회원이 없습니다."),
    MEMBER_NOT_FOUND(400, "M001", "존재하지 않는 회원입니다."),
    MEMBER_REGISTER_FAIL(400, "M002", "회원 생성에 실패했습니다."),
    SUBSCRIBER_INFO_NOT_FOUND(400, "M003", "존재하지 않는 구독 정보입니다."),

    // Order
    ORDER_FAIL(400, "O001", "주문에 실패했습니다."),
    ORDER_NOT_FOUND(400, "O001", "존재하지 않는 주문입니다."),
    PATH_NOT_RESOLVED(400, "O002", "경로를 찾을 수 없습니다."),

    // Product
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

    //OrderNumber
    INVALID_ORDER_TYPE(400, "C005", "잘못된 오더 타입입니다.");


    private final int status;
    private final String code;
    private final String message;

}
