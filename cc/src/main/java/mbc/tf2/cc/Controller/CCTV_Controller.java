package mbc.tf2.cc.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class CCTV_Controller {

    @Value("${apiKey}")
    private String apiKey;

    @Value("${roadtype}")
    private String roadtype;

    @Value("${cctvType}")
    private String cctvType;

    @Value("${minX}")
    private String minX;

    @Value("${maxX}")
    private String maxX;

    @Value("${minY}")
    private String minY;

    @Value("${maxY}")
    private String maxY;

    @Value("${getType}")
    private String getType;

    @GetMapping("/")
    public String cctv(Model mo) {
        String URL = "https://openapi.its.go.kr:9443/cctvInfo?apiKey=" + apiKey
                + "&type=" + roadtype
                + "&cctvType=" + cctvType
                + "&minX=" + minX
                + "&maxX=" + maxX
                + "&minY=" + minY
                + "&maxY=" + maxY
                + "&getType=" + getType;

        RestTemplate restTemplate = new RestTemplate();
        String jsonResponse = restTemplate.getForObject(URL, String.class);

        List<Map<String, Object>> cctvList = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode dataArray = root.path("response").path("data");

            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    Map<String, Object> cctvData = objectMapper.convertValue(node, Map.class);
                    cctvList.add(cctvData);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        mo.addAttribute("cctvList", cctvList);
        return "home";
    }

}
