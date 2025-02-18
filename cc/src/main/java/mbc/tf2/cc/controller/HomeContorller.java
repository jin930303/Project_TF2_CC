package mbc.tf2.cc.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeContorller {

    @GetMapping("/")
    public String home() {

        return "main";
    }

    @GetMapping("/main")
    public String main() {

        return "main";
    }
}
