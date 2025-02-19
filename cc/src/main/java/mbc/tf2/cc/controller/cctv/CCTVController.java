package mbc.tf2.cc.controller.cctv;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mbc.tf2.cc.dto.cctv.CCTVDTO;
import mbc.tf2.cc.dto.cctv.CCTV_Auth_DTO;
import mbc.tf2.cc.entity.cctv.CCTVEntity;
import mbc.tf2.cc.service.cctv.CCTVService;
import mbc.tf2.cc.service.cctv.CCTV_Auth_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class CCTVController {

    @Value("${apiKey}")
    private String apiKey;

    @Value("${cctvType}")
    private String cctvType;

    @Value("${getType}")
    private String getType;

    @Autowired
    CCTVService cs;

    @Autowired
    CCTV_Auth_Service cas;

    @GetMapping("/admin/cctv_manage")
    public String cctv_manage(Model mo) {
        List<CCTVEntity> cctv_list= cs.select_cctv();
        List<CCTV_Auth_DTO> user_cctv_list_all= cas.select_user_cctv_all();
        mo.addAttribute("user_cctv_list_all", user_cctv_list_all);
        mo.addAttribute("cctv_list", cctv_list);
        return "cctv_manage";
    }

    @PostMapping("/admin/cctv_type")
    public String cctv(Model mo,
                       @RequestParam("cctv_location") String cctv_location,
                       @RequestParam("roadtype") String roadtype) {

        String minX = "", maxX = "", minY = "", maxY = "", location = "";

        //... 설정된 권역별 cctv 위도 및 경도 최소/최대값 설정
        if (cctv_location.equals("s_i_gg")) {
            minX = "126.346200"; maxX = "127.847900"; minY = "36.899800"; maxY = "38.276600";
        }
        else if (cctv_location.equals("gw")) {
            minX = "127.082200"; maxX = "129.437800"; minY = "37.047600"; maxY = "38.623800";
        }
        else if (cctv_location.equals("d_cn")) {
            minX = "126.117400"; maxX = "127.633500"; minY = "35.983700"; maxY = "37.067400";
        }
        else if (cctv_location.equals("cb")) {
            minX = "127.253200"; maxX = "128.648400"; minY = "36.01600"; maxY = "37.25900";
        }
        else if (cctv_location.equals("d_gb")) {
            minX = "127.755900"; maxX = "129.652500"; minY = "35.570900"; maxY = "37.261300";
        }
        else if (cctv_location.equals("b_u_gn")) {
            minX = "127.556900"; maxX = "129.572900"; minY = "34.624900"; maxY = "35.918600";
        }
        else if (cctv_location.equals("g_jn")) {
            minX = "125.777400"; maxX = "127.809800"; minY = "34.109600"; maxY = "35.518900";
        }
        else if (cctv_location.equals("jb")) {
            minX = "126.370300"; maxX = "127.913900"; minY = "35.283400"; maxY = "36.168600";
        }
        else if (cctv_location.equals("jj")) {
            minX = "126.096200"; maxX = "127.005100"; minY = "33.175100"; maxY = "33.642000";
        }

        //... API URL 설정
        String URL = "https://openapi.its.go.kr:9443/cctvInfo?apiKey=" + apiKey
                + "&type=" + roadtype
                + "&cctvType=" + cctvType
                + "&minX=" + minX
                + "&maxX=" + maxX
                + "&minY=" + minY
                + "&maxY=" + maxY
                + "&getType=" + getType;

        RestTemplate restTemplate = new RestTemplate();   //... RESTful 웹서비스 통신 설정
        String jsonResponse = restTemplate.getForObject(URL, String.class);   //... API URL로 GET 요청 및 응답받은 JSON 데이터 저장

        List<Map<String, Object>> cctvList = new ArrayList<>();   //... JSON 데이터의 각 항목을 Map으로 저장

        try {
            ObjectMapper objectMapper = new ObjectMapper();   //... ObjectMapper 설정
            JsonNode root = objectMapper.readTree(jsonResponse);   //... JSON 문자열을 JsonNode 트리구조로 변환
            JsonNode dataArray = root.path("response").path("data");

            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    Map<String, Object> cctvData = objectMapper.convertValue(node, Map.class);   //... Map 형태로 변환
                    cctvList.add(cctvData);   //... cctvList에 데이터 추가
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        List<CCTVEntity> cctv_list= cs.select_cctv();
        List<CCTV_Auth_DTO> user_cctv_list_all= cas.select_user_cctv_all();
        mo.addAttribute("cctv_list", cctv_list);
        mo.addAttribute("cctvList", cctvList);
        mo.addAttribute("selected_cctv_location", cctv_location);
        mo.addAttribute("user_cctv_list_all", user_cctv_list_all);
        return "cctv_manage";
    }

    @PostMapping("/admin/cctv_select")
    public String cctv_select(@RequestParam("cctv_url") String cctv_url,
                              @RequestParam("selected_cctv_location") String cctv_location,
                              @RequestParam("cctv_name") String cctv_name,
                              CCTVDTO dto, Model mo) {
        if (cctv_location.equals("s_i_gg")) {
            cctv_location = "서울/인천/경기도";
        }
        else if (cctv_location.equals("gw")) {
            cctv_location = "강원도";
        }
        else if (cctv_location.equals("d_cn")) {
            cctv_location = "대전/충청남도";
        }
        else if (cctv_location.equals("cb")) {
            cctv_location = "충청북도";
        }
        else if (cctv_location.equals("d_gb")) {
            cctv_location = "대구/경상북도";
        }
        else if (cctv_location.equals("b_u_gn")) {
            cctv_location = "부산/울산/경상남도";
        }
        else if (cctv_location.equals("g_jn")) {
            cctv_location = "광주/전라남도";
        }
        else if (cctv_location.equals("jb")) {
            cctv_location = "전라북도";
        }
        else if (cctv_location.equals("jj")) {
            cctv_location = "제주도";
        }
        dto.setCctv_location(cctv_location);
        dto.setCctv_name(cctv_name);
        dto.setCctvurl(cctv_url);
        cs.insert_cctv(dto);
        List<CCTVEntity> cctv_list= cs.select_cctv();
        mo.addAttribute("cctv_list", cctv_list);
        return "redirect:/admin/cctv_manage";
    }

    @GetMapping("/admin/cctv_list_del")
    public String cctv_list_del(Model mo, @RequestParam("cctv_name") String cctv_name) {
        cs.delete_cctv_list(cctv_name);
        cas.delete_cctv_list_all(cctv_name);
        List<CCTVEntity> cctv_list= cs.select_cctv();
        mo.addAttribute("cctv_list", cctv_list);
        return "redirect:/admin/cctv_manage";
    }

    @GetMapping("/admin/cctv_confirm")
    public String cctv_confirm(@RequestParam("cctv_auth_num") long cctv_auth_num, CCTV_Auth_DTO dto) {
        dto.setCctv_add_confirm("승인");
        cas.auth_update_confirm(cctv_auth_num);
        return "redirect:/admin/cctv_manage";
    }

    @GetMapping("/admin/cctv_wait")
    public String cctv_wait(@RequestParam("cctv_auth_num") long cctv_auth_num, CCTV_Auth_DTO dto) {
        dto.setCctv_add_confirm("보류");
        cas.auth_update_wait(cctv_auth_num);
        return "redirect:/admin/cctv_manage";
    }
}
