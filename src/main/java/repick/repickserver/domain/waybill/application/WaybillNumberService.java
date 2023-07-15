package repick.repickserver.domain.waybill.application;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import repick.repickserver.domain.waybill.dao.WaybillNumberRepository;
import repick.repickserver.domain.waybill.domain.WaybillNumber;
import repick.repickserver.domain.waybill.dto.WaybillNumberRequest;
import repick.repickserver.domain.waybill.dto.WaybillNumberResponse;
import repick.repickserver.global.config.SweetTrackerProperties;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WaybillNumberService {

    private final WaybillNumberRepository waybillNumberRepository;
    private final SweetTrackerProperties sweetTrackerProperties;

    /**
     * <h1>운송장 번호 등록</h1>
     * @param request (code, waybillNumber, orderNumber)
     * @return true
     * @author seochanhyeok
     */
    public Boolean postWaybillNumber(WaybillNumberRequest request) {
        WaybillNumber waybillNumber = WaybillNumber.builder()
                .code(request.getCode())
                .waybillNumber(request.getWaybillNumber())
                .orderNumber(request.getOrderNumber())
                .build();

        waybillNumberRepository.save(waybillNumber);

        return true;
    }

    /**
     * <h1>주문 번호로 운송장 번호 조회</h1>
     * @param orderNumber (주문 번호)
     * @return waybillNumberResponse (code, waybillNumber, orderNumber)
     * @author seochanhyeok
     */
    public WaybillNumberResponse getWaybillNumber(String orderNumber) {
        WaybillNumber waybillNumber = waybillNumberRepository.findByOrderNumber(orderNumber);
        return WaybillNumberResponse.builder()
                .code(waybillNumber.getCode())
                .waybillNumber(waybillNumber.getWaybillNumber())
                .orderNumber(waybillNumber.getOrderNumber())
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
        WaybillNumber waybillNumber = waybillNumberRepository.findByOrderNumber(orderNumber);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(new HttpHeaders());

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "http://info.sweettracker.co.kr/api/v1/trackingInfo?t_key=" + sweetTrackerProperties.getApiKey() +
                        "&t_code=" + waybillNumber.getCode() +
                        "&t_invoice=" + waybillNumber.getWaybillNumber(),
                HttpMethod.GET,
                httpEntity,
                String.class
        );

        return response.getBody();
    }
}
