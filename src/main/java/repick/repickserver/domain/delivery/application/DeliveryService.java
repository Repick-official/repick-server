package repick.repickserver.domain.delivery.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import repick.repickserver.domain.delivery.dao.DeliveryRepository;
import repick.repickserver.domain.delivery.domain.Delivery;
import repick.repickserver.domain.delivery.dto.DeliveryRequest;
import repick.repickserver.domain.delivery.dto.DeliveryResponse;
import repick.repickserver.domain.ordernumber.dao.OrderNumberReository;
import repick.repickserver.global.config.SweetTrackerProperties;
import repick.repickserver.global.error.exception.CustomException;

import javax.transaction.Transactional;

import static repick.repickserver.global.error.exception.ErrorCode.ORDER_NOT_FOUND;
import static repick.repickserver.global.error.exception.ErrorCode.WAYBILL_NUMBER_NOT_REGISTERED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final SweetTrackerProperties sweetTrackerProperties;
    private final OrderNumberReository orderNumberReository;

    /**
     * <h1>운송장 번호 등록</h1>
     * @param request (code, waybillNumber, orderNumber)
     * @return DeliveryResponse (code, waybillNumber, orderNumber)
     * @exception CustomException (ORDER_NOT_FOUND)
     * @author seochanhyeok
     */
    public DeliveryResponse postWaybillNumber(DeliveryRequest request) {

        // 주문번호가 유효하지 않는 경우 예외처리
        if (!orderNumberReository.existsByOrderNumber(request.getOrderNumber())) {
            throw new CustomException(ORDER_NOT_FOUND);
        }

        Delivery delivery = Delivery.builder()
                .code(request.getCode())
                .waybillNumber(request.getWaybillNumber())
                .orderNumber(request.getOrderNumber())
                .build();

        deliveryRepository.save(delivery);

        return DeliveryResponse.builder()
                .code(delivery.getCode())
                .waybillNumber(delivery.getWaybillNumber())
                .orderNumber(delivery.getOrderNumber())
                .build();
    }

    /**
     * <h1>주문 번호로 운송장 번호 조회</h1>
     * @param orderNumber (주문 번호)
     * @return deliveryResponse (code, waybillNumber, orderNumber)
     * @author seochanhyeok
     */
    public DeliveryResponse getWaybillNumber(String orderNumber) {

        Delivery delivery = deliveryRepository.findByOrderNumber(orderNumber);

        // 운송장번호가 유효하지 않는 경우 예외처리
        if (delivery == null) throw new CustomException(WAYBILL_NUMBER_NOT_REGISTERED);

        return DeliveryResponse.builder()
                .code(delivery.getCode())
                .waybillNumber(delivery.getWaybillNumber())
                .orderNumber(delivery.getOrderNumber())
                .build();
    }

    /**
     * <h1>주문 번호로 배송조회</h1>
     * @param orderNumber (주문 번호)
     * @return 배송조회 결과 (json)
     * @see <a href="http://info.sweettracker.co.kr/apidoc/">SweetTracker API</a>
     * @author seochanhyeok
     */
    public String getWaybillStatus(String orderNumber) {
        Delivery delivery = deliveryRepository.findByOrderNumber(orderNumber);

        // 운송장번호가 유효하지 않는 경우 예외처리
        if (delivery == null) throw new CustomException(WAYBILL_NUMBER_NOT_REGISTERED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(new HttpHeaders());

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "http://info.sweettracker.co.kr/api/v1/trackingInfo?t_key=" + sweetTrackerProperties.getApiKey() +
                        "&t_code=" + delivery.getCode() +
                        "&t_invoice=" + delivery.getWaybillNumber(),
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        return response.getBody();
    }
}
