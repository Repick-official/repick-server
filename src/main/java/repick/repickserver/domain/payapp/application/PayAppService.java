package repick.repickserver.domain.payapp.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.payapp.domain.PayApp;
import repick.repickserver.domain.payapp.dto.PayAppResponse;
import repick.repickserver.domain.payapp.dto.RegisterPayAppRequest;
import repick.repickserver.domain.payapp.repository.PayAppRepository;
import repick.repickserver.domain.payapp.validator.PayAppValidator;
import repick.repickserver.domain.product.dao.ProductRepository;
import repick.repickserver.domain.product.domain.Product;
import repick.repickserver.global.error.exception.CustomException;

import static repick.repickserver.global.error.exception.ErrorCode.LINK_NOT_FOUND;
import static repick.repickserver.global.error.exception.ErrorCode.PRODUCT_NOT_FOUND;

@Service @RequiredArgsConstructor
public class PayAppService {

    private final ProductRepository productRepository;
    private final PayAppRepository payAppRepository;

    public boolean registerPayAppUrl(RegisterPayAppRequest request) {

        PayAppValidator.validatePayAppUrl(request.getUrl(), request.getPrice());

        PayApp payApp = PayApp.of(request);

        payAppRepository.save(payApp);

        return true;

    }

    public PayAppResponse getPayAppUrl(Long id) {
        System.out.println("PayAppService.getPayAppUrl");
        Product product = productRepository.findById(id).orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));
        System.out.println("product.getPrice() = " + product.getPrice());
        PayApp payApp = payAppRepository.findByPrice(product.getPrice()).orElseThrow(() -> new CustomException(LINK_NOT_FOUND));
        System.out.println("PayAppService.getPayAppUrl");
        return PayAppResponse.from(payApp);

    }
}
