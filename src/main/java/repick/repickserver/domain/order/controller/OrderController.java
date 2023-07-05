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
        // 토큰으로부터 이메일을 얻음
        token = token.split(" ")[1].trim();
        String email = jwtProvider.getEmail(token);
        // 이메일로 멤버 인스턴스를 얻음
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new Exception("회원정보 오류: 이메일에 맞는 회원을 찾을 수 없습니다."));
        return new ResponseEntity<>(orderService.postSellOrder(request, member), HttpStatus.OK);
    }

}
