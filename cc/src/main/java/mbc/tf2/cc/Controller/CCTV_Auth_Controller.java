package mbc.tf2.cc.Controller;

import mbc.tf2.cc.Entity.CCTV_Auth_Entity;
import mbc.tf2.cc.Service.CCTV_Auth_Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @GetMapping("/detect")
    public String detect(Model mo) {
        String user_id = SecurityContextHolder.getContext().getAuthentication().getName();
        List<CCTV_Auth_Entity> user_cctv_list= cas.select_user_cctv(user_id);
        mo.addAttribute("user_cctv_list", user_cctv_list);
        return "detect";
    }
}
