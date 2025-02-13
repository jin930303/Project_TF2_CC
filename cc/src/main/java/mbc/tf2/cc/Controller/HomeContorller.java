package mbc.tf2.cc.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeContorller {

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/board")
    public String board(){

        return "board";
    }
}
