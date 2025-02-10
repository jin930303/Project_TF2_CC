package mbc.tf2.cc.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeContorller {

    @GetMapping("/home")
    public String home(){
        return "home";
    }

}
