package repick.repickserver.domain.payapp.api;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.payapp.application.PayAppService;
import repick.repickserver.domain.payapp.dto.PayAppResponse;
import repick.repickserver.domain.payapp.dto.RegisterPayAppRequest;

@RestController("/payapp") @RequiredArgsConstructor
public class PayAppController {

    private final PayAppService payAppService;

    // get product ID as path variable
    @GetMapping("price/{productId}")
    public PayAppResponse getPayAppUrl(@PathVariable("productId") Long productId) {
        return payAppService.getPayAppUrl(productId);
    }

    @PostMapping("/register")
    public boolean registerPayAppUrl(@RequestBody RegisterPayAppRequest request) {
        return payAppService.registerPayAppUrl(request);
    }
}
