// controller/RouteController.java
package com.example.instagramclone.controller.routes;

import com.example.instagramclone.constant.AuthConstants;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class RouteController {

    /*
        로그인 여부에 따라 다른 페이지를 라우팅해야 함
        어떻게 로그인 여부를 알 수 있을지... -> 세션에 LOGIN_MEMBER 객체가 있는지 확인
     */
    @GetMapping("/")
    public String index(HttpSession session) {
        Object loginMember = session.getAttribute(AuthConstants.SESSION_KEY);

        log.info("메인페이지에서 인증된 사용자 정보: {}", loginMember);

        if (loginMember == null) {
            return "auth/login";
        }

        return "index";
    }

    // 회원가입 페이지 열기
    @GetMapping("/signup")
    public String signUp() {
        return "auth/signup";
    }

    // 프로필 페이지 열기
    @GetMapping("/{username}")
    public String profilePage() {
        return "components/profile-page";
    }

    // 해시태그 페이지 열기
    @GetMapping("/explore/search/keyword/")
    public String hashtag() {
        return "components/hashtag-search";
    }

}
