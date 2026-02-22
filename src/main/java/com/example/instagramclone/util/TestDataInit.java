package com.example.instagramclone.util;

import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataInit implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (memberRepository.count() > 0) return;

        List<String> usernames = List.of("kuromi", "mamel", "pikachu", "kitty", "heartping");
        List<String> names = List.of("쿠로미", "마이멜로디", "피카츄", "키티", "하츄핑");
        String password = passwordEncoder.encode("abc1234!");

        for (int i = 0; i < usernames.size(); i++) {
            Member member = Member.builder()
                    .username(usernames.get(i))
                    .password(password)
                    .name(names.get(i))
                    .email(usernames.get(i) + "@test.com")
                    .build();

            memberRepository.save(member);
        }
        System.out.println("테스트용 계정 5개 세팅 완료!");
    }
}
