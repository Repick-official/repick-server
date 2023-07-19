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
import repick.repickserver.global.config.SweetTrackerProperties;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final SweetTrackerProperties sweetTrackerProperties;

    /**
     * <h1>운송장 번호 등록</h1>
     * @param request (code, waybillNumber, orderNumber)
     * @return true
     * @author seochanhyeok
     */
    public Boolean postWaybillNumber(DeliveryRequest request) {
        Delivery delivery = Delivery.builder()
                .code(request.getCode())
                .waybillNumber(request.getWaybillNumber())
                .orderNumber(request.getOrderNumber())
                .build();

        deliveryRepository.save(delivery);

        return true;
    }

    /**
     * <h1>주문 번호로 운송장 번호 조회</h1>
     * @param orderNumber (주문 번호)
     * @return deliveryResponse (code, waybillNumber, orderNumber)
     * @author seochanhyeok
     */
    public DeliveryResponse getWaybillNumber(String orderNumber) {
        Delivery delivery = deliveryRepository.findByOrderNumber(orderNumber);
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
