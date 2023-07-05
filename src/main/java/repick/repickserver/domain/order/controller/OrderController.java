package repick.repickserver.domain.order.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repick.repickserver.domain.member.dao.MemberRepository;
import repick.repickserver.domain.member.domain.Member;
import repick.repickserver.domain.order.application.OrderService;
import repick.repickserver.domain.order.dto.SellOrderRequest;
import repick.repickserver.global.jwt.JwtProvider;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/sell")
    public void getSellOrders() {

    }

    @PostMapping(value = "/user/sell")
    public ResponseEntity<Boolean> postSellOrder(@RequestBody SellOrderRequest request, @RequestHeader("Authorization") String token) throws Exception {
        Member member = jwtProvider.getMember(token);
        return new ResponseEntity<>(orderService.postSellOrder(request, member), HttpStatus.OK);
    }

}
