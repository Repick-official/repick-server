package repick.repickserver.domain.payapp.validator;

import org.springframework.stereotype.Component;
import repick.repickserver.domain.payapp.domain.PayApp;

@Component
public class PayAppValidator {

    public static void validatePayAppUrl(String url, Long price) {
        PayApp.validatePayApp(url, price);
    }
}
