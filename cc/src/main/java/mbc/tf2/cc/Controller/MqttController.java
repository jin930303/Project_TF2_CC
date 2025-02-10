package mbc.tf2.cc.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class MqttController {

    private static final String BROKER_URL = "tcp://localhost:1883"; // MQTT 브로커 주소
    private static final String TOPIC = "/cctv/objects"; // 파이썬이 구독할 토픽
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/cctv_link")
    public RedirectView cctv_link(@RequestParam("cctv_url") String cctv_url, @RequestParam("detect_objects") String detect_objects) {
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
            payloadMap.put("detect_objects", detect_objects);
            String payload = objectMapper.writeValueAsString(payloadMap);

            // MQTT 메시지 발행
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1);
            client.publish(TOPIC, message);

            // 연결 종료
            client.disconnect();
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/");
            return redirectView;

        } catch (MqttException | com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
            RedirectView redirectView = new RedirectView();
            redirectView.setUrl("/");
            return redirectView;        }
    }
}