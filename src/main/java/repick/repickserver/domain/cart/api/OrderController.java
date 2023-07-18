package repick.repickserver.domain.cart.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repick.repickserver.domain.ordernumber.application.OrderNumberService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderNumberService orderNumberService;
}
