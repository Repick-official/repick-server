package repick.repickserver.domain.payapp.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import repick.repickserver.domain.payapp.domain.PayApp;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayAppResponse {

    private String url;

    @Builder
    private PayAppResponse(String url) {
        this.url = url;
    }

    public static PayAppResponse from(PayApp payApp) {
        return PayAppResponse.builder()
                .url(payApp.getUrl())
                .build();
    }
}
