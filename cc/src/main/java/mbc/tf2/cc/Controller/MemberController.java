package mbc.tf2.cc.Controller;


import mbc.tf2.cc.memberDTO.Member;
import mbc.tf2.cc.memberDTO.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("member", new Member());
        return "register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute Member member, Model model) {
        String errorMessage = memberService.registerMember(member);
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "register"; // 오류가 있으면 등록 페이지로 돌아가고 오류 메시지 전달
        }
        return "redirect:/member/login";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String memberId, @RequestParam String password, Model model) {
        if (memberService.validateLogin(memberId, password)) {
            return "home"; // 로그인 성공 시 홈으로 이동
        } else {
            model.addAttribute("error", "Invalid Credentials");
            return "login"; // 로그인 실패 시 오류 메시지와 함께 로그인 페이지로 돌아가
        }
    }
}
