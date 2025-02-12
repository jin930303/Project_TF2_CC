package mbc.tf2.cc.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class CCTV_Controller {

    @Value("${apiKey}")
    private String apiKey;

    @Value("${cctvType}")
    private String cctvType;

    @Value("${getType}")
    private String getType;

    @GetMapping("/cctv_type")
    public String cctv(Model mo,
                       @RequestParam("cctv_location") String cctv_location,
                       @RequestParam("roadtype") String roadtype) {

        String minX = "", maxX = "", minY = "", maxY = "";

        if (cctv_location.equals("s_i_gg")) {
            minX = "126.346200";
            maxX = "127.847900";
            minY = "36.899800";
            maxY = "38.276600";
        }
        else if (cctv_location.equals("gw")) {
            minX = "127.082200";
            maxX = "129.437800";
            minY = "37.047600";
            maxY = "38.623800";
        }
        else if (cctv_location.equals("d_cn")) {
            minX = "126.117400";
            maxX = "127.633500";
            minY = "35.983700";
            maxY = "37.067400";
        }
        else if (cctv_location.equals("cb")) {
            minX = "127.253200";
            maxX = "128.648400";
            minY = "36.01600";
            maxY = "37.25900";
        }
        else if (cctv_location.equals("d_gb")) {
            minX = "127.755900";
            maxX = "129.652500";
            minY = "35.570900";
            maxY = "37.261300";
        }
        else if (cctv_location.equals("b_u_gn")) {
            minX = "127.556900";
            maxX = "129.572900";
            minY = "34.624900";
            maxY = "35.918600";
        }
        else if (cctv_location.equals("g_jn")) {
            minX = "125.777400";
            maxX = "127.809800";
            minY = "34.109600";
            maxY = "35.518900";
        }
        else if (cctv_location.equals("jb")) {
            minX = "126.370300";
            maxX = "127.913900";
            minY = "35.283400";
            maxY = "36.168600";
        }
        else if (cctv_location.equals("jj")) {
            minX = "126.096200";
            maxX = "127.005100";
            minY = "33.175100";
            maxY = "33.642000";
        }

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
