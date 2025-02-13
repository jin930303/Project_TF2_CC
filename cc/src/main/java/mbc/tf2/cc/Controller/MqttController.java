package mbc.tf2.cc.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class MqttController {

    @Value("${BROKER_URL}")
    private String BROKER_URL;

    @Value("${TOPIC}")
    private String TOPIC;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/cctv_link")
    public String cctv_link(@RequestParam("cctv_url") String cctv_url,
                                  @RequestParam("cctv_name") String cctv_name,
                                  @RequestParam("detect_available") String detect_available,
                                  @RequestParam("detect_objects") String detect_objects,
                                  @RequestParam("cctv_location") String cctv_location,
                                  @RequestParam("roadtype") String roadtype,
                                  RedirectAttributes redirectAttributes) {
        try {
            // ✅ UUID를 이용하여 Client ID 동적으로 생성
            String clientId = "spring-mqtt-" + UUID.randomUUID();

            // MQTT 클라이언트 생성
            MqttClient client = new MqttClient(BROKER_URL, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);

            // JSON 형식으로 메시지 구성
            Map<String, String> payloadMap = new HashMap<>();
            payloadMap.put("cctv_url", cctv_url);
            payloadMap.put("cctv_name", cctv_name);
            payloadMap.put("detect_objects", detect_objects);
            payloadMap.put("detect_available", detect_available);
            String payload = objectMapper.writeValueAsString(payloadMap);

            // MQTT 메시지 발행
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            client.publish(TOPIC, message);

            // 연결 종료
            client.disconnect();
        } catch (MqttException | JsonProcessingException e) {
            e.printStackTrace();
        }
        String send_object = "";
        if (detect_objects.equals("total_object")) {
            send_object = "전체";
        }
        else if (detect_objects.equals("car")) {
            send_object = "승용차";
        }
        else if (detect_objects.equals("bus")) {
            send_object = "버스";
        }
        else if (detect_objects.equals("truck")) {
            send_object = "트럭";
        }
        else if (detect_objects.equals("pickup_truck")) {
            send_object = "픽업트럭";
        }
        else if (detect_objects.equals("motor_cycle")) {
            send_object = "오토바이";
        }
        else if (detect_objects.equals("etc")) {
            send_object = "기타";
        }

        // ✅ 이전 URL을 유지하며 리다이렉트
        redirectAttributes.addFlashAttribute("cctv_name", cctv_name);
        redirectAttributes.addFlashAttribute("send_object", send_object);

        // ✅ 리다이렉트 URL 설정
        return "redirect:/cctv_type?cctv_location=" + cctv_location + "&roadtype=" + roadtype;
    }

}