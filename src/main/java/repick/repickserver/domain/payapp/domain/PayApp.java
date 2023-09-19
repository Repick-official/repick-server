package repick.repickserver.domain.payapp.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.payapp.dto.RegisterPayAppRequest;
import repick.repickserver.global.error.exception.CustomException;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static repick.repickserver.global.error.exception.ErrorCode.LINK_NOT_FOUND;
import static repick.repickserver.global.error.exception.ErrorCode.PRICE_INVALID;

@Entity @Data @NoArgsConstructor
public class PayApp {

    @Id @GeneratedValue
    private Long id;
    private String url;
    private Long price;

    @Builder
    private PayApp(String url, Long price) {
        this.url = url;
        this.price = price;
    }

    public static PayApp of(RegisterPayAppRequest request) {
        return PayApp.builder()
                .url(request.getUrl())
                .price(request.getPrice())
                .build();
    }

    public static void validatePayApp(String url, Long price) {
        if (url == null || url.isEmpty()) {
            throw new CustomException(LINK_NOT_FOUND);
        }
        if (price == null || price < 0) {
            throw new CustomException(PRICE_INVALID);
        }
    }
}
