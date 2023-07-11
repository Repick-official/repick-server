package repick.repickserver.domain.order.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.order.application.SellOrderService;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.domain.order.dto.SellOrderResponse;
import repick.repickserver.global.jwt.JwtProvider;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class SellOrderController {

    private final SellOrderService orderService;

    @GetMapping(value = "/sell/{state}")
    public ResponseEntity<List<SellOrderResponse>> getSellOrders(@PathVariable("state") String state, @RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(orderService.getSellOrders(state, token), HttpStatus.OK);
    }

    @PostMapping(value = "/sell")
    public ResponseEntity<Boolean> postSellOrder(@RequestBody SellOrderRequest request, @RequestHeader("Authorization") String token) throws Exception {
        return new ResponseEntity<>(orderService.postSellOrder(request, token), HttpStatus.OK);
    }

}
