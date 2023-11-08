package repick.repickserver.infra.slack.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import repick.repickserver.domain.order.domain.OrderProduct;
import repick.repickserver.domain.order.dto.OrderRequest;
import repick.repickserver.domain.sellorder.domain.SellOrder;
import repick.repickserver.domain.sellorder.dto.SellOrderRequest;

import java.util.List;

@Service @RequiredArgsConstructor
public class SlackMapper {

    public String toOrderSlackNoticeString(String orderNumber, OrderRequest orderRequest, List<OrderProduct> orderProducts) {
        StringBuilder sb = new StringBuilder();
        sb.append("주문 신청이 들어왔습니다.\n");
        sb.append("주문 번호: ").append(orderNumber).append("\n");
        sb.append("신청자: ").append(orderRequest.getPersonName()).append("\n");
        sb.append("연락처: ").append(orderRequest.getPhoneNumber()).append("\n");
        sb.append("주소: ").append(orderRequest.getAddress().getMainAddress()).append("\n");
        sb.append("상세 주소: ").append(orderRequest.getAddress().getDetailAddress()).append("\n");
        sb.append("우편 번호: ").append(orderRequest.getAddress().getZipCode()).append("\n");
        sb.append("주문 상품: ").append("\n");
        orderProducts.forEach(orderProduct ->
                sb.append(orderProduct.getProduct().getName()).append(" ")
                        .append(orderProduct.getProduct().getPrice()).append(" ")
                        .append(orderProduct.getProduct().getProductNumber()).append("\n"));
        return sb.toString();
    }

    public String toSellOrderSlackNoticeString(SellOrderRequest request, String orderNumber) {
        return "판매 수거 요청이 들어왔습니다.\n" +
                "주문번호: " + orderNumber + "\n" +
                "이름: " + request.getName() + "\n" +
                "연락처: " + request.getPhoneNumber() + "\n" +
                "주소: " + request.getAddress().getMainAddress() + "\n" +
                "상세주소: " + request.getAddress().getDetailAddress() + "\n" +
                "우편번호: " + request.getAddress().getZipCode() + "\n" +
                "수거 시 희망사항: " + request.getRequestDetail() + "\n" +
                "수거 희망일: " + request.getReturnDate() + "\n" +
                "의류 수량: " + request.getProductQuantity() + "\n" +
                "리픽백 수량: " + request.getBagQuantity();
    }

    public String toSellOrderBagReadySlackNoticeString(SellOrder sellOrder) {
        return "리픽백 배출이 완료되었습니다.\n" +
                "주문번호: " + sellOrder.getOrderNumber() + "\n" +
                "이름: " + sellOrder.getName() + "\n" +
                "연락처: " + sellOrder.getPhoneNumber() + "\n" +
                "주소: " + sellOrder.getAddress().getMainAddress() + "\n" +
                "상세주소: " + sellOrder.getAddress().getDetailAddress() + "\n" +
                "우편번호: " + sellOrder.getAddress().getZipCode() + "\n" +
                "수거 시 희망사항: " + sellOrder.getRequestDetail() + "\n" +
                "수거 희망일: " + sellOrder.getReturnDate() + "\n" +
                "의류 수량: " + sellOrder.getProductQuantity() + "\n" +
                "리픽백 수량: " + sellOrder.getBagQuantity();
    }

}
