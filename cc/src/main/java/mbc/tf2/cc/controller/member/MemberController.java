package mbc.tf2.cc.controller.member;


import mbc.tf2.cc.entity.member.MemberEntity;
import mbc.tf2.cc.service.member.MemberService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 페이지
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("member", new MemberEntity());
        return "register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String register(@ModelAttribute MemberEntity memberEntity, Model model) {
        String errorMessage = memberService.registerMember(memberEntity);
        if (errorMessage != null) {
            model.addAttribute("error", errorMessage);
            return "register"; // 오류가 있으면 등록 페이지로 돌아가고 오류 메시지 전달
        }
        return "redirect:/login";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    //로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String memberId, @RequestParam String password, Model model) {
        if (memberService.validateLogin(memberId, password)) {
            return "redirect:/main"; // 로그인 성공 시 메인페이지로 이동
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login"; // 로그인 실패 시 로그인 페이지로 돌아가기
        }
    }
    // 마이페이지 (회원 정보 수정 및 비밀번호 변경)
    @GetMapping("/user/mypage")
    public String showMyPage(Model model) {
        MemberEntity member = memberService.getCurrentMember(); // 로그인된 사용자 정보 가져오기
        model.addAttribute("member", member);
        return "mypage"; // mypage.html
    }

    // 회원 정보 수정 페이지 이동 (GET 요청)
    @GetMapping("/user/updateUser")
    public String showUpdateUserPage(Model model) {
        MemberEntity member = memberService.getCurrentMember(); // 로그인된 사용자 정보 가져오기
        model.addAttribute("member", member); // 수정할 정보를 폼에 전달
        return "updateUser"; // updateUser.html로 이동
    }

    // 회원 정보 수정 처리
    @PostMapping("/user/updateUser")
    public String updateUser(@ModelAttribute MemberEntity member, Model model) {
        // 로그인된 사용자 정보로 수정 처리
        try {
            memberService.updateMemberInfo(member); // 회원 정보 업데이트
            return "redirect:/user/mypage"; // 회원 정보 수정 성공시 mypage로 이동
        } catch (Exception e) {
            model.addAttribute("error", "회원 정보 수정 실패");
            return "updateUser"; // 실패시 다시 updateUser 페이지로 돌아감
        }
    }

    // 비밀번호 변경 페이지
    @GetMapping("/user/changePassword")
    public String showChangePasswordForm() {
        return "changePassword"; // changePassword.html 페이지로 이동
    }

    // 비밀번호 변경 처리
    @PostMapping("/user/changePassword")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Model model) {
        try {

            memberService.changePassword(oldPassword, newPassword); // 비밀번호 변경 처리
            return "redirect:/user/mypage"; // 비밀번호 변경 성공 시 mypage로 이동
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage()); // 에러 메시지 추가
            return "changePassword"; // 실패 시 changePassword 페이지로 돌아가기
        }
    }
}
