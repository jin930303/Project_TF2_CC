package mbc.tf2.cc.controller.cctv;

import mbc.tf2.cc.dto.cctv.CCTV_Auth_DTO;
import mbc.tf2.cc.entity.cctv.CCTVEntity;
import mbc.tf2.cc.entity.cctv.CCTV_Auth_Entity;
import mbc.tf2.cc.service.cctv.CCTVService;
import mbc.tf2.cc.service.cctv.CCTV_Auth_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CCTV_Auth_Controller {

    @Value("${apiKey}")
    private String apiKey;

    @Value("${cctvType}")
    private String cctvType;

    @Value("${getType}")
    private String getType;

    @Autowired
    CCTV_Auth_Service cas;

    @Autowired
    CCTVService cs;

    @GetMapping("/detect")
    public String detect(Model mo) {
        String user_id = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CCTV_Auth_DTO> user_cctv_list= cas.select_user_cctv(user_id);
        mo.addAttribute("user_cctv_list", user_cctv_list);
        return "detect";
    }

    @GetMapping("/cctv_add_auth")
    public String cctv_add_auth(Model mo) {
        List<CCTVEntity> cctv_list= cs.select_cctv();
        mo.addAttribute("cctv_list", cctv_list);
        return "cctv_add_auth";
    }

    @GetMapping("/cctv_auth_select")
    public String cctv_auth_select(@Param("cctv_name") String cctv_name, CCTV_Auth_DTO dto) {
        String user_id = SecurityContextHolder.getContext().getAuthentication().getName();
        dto.setId(user_id);
        dto.setCctv_name(cctv_name);
        CCTV_Auth_Entity cae = dto.entity();
        cas.insert_cctv_auth(cae);
        return "cctv_add_auth";
    }
}
