package com.ohgiraffers.comprehensive.config;

import com.ohgiraffers.comprehensive.member.service.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*시큐리티 설정 활성화 및 bean 등록 가능*/
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationService authenticationService;//의존성주입

    public SecurityConfig(AuthenticationService authenticationService){

        this.authenticationService = authenticationService;
    }

    /*비밀번호 암호와에 사용할 객체 BCryptPasswordEncoder bean등록*/

    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();

        }
    /*Http 요청에 대한 설정을 SecurityFilterChain에 설정*/
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return  http

                /*CSRF 공격 방지는 기본적으로 활성화 되어 있어 비활성화 처리*/
                .csrf()
                .disable()
                /*요청에 대한 권한 체크 */
                .authorizeHttpRequests()
                /*hasRole에 전달하는 값은 "ROLE_"가 자동으로 앞에 붙는다.*/
                .antMatchers("/css/**", "/js/**",  "/images/**").permitAll()
                .antMatchers("/board/**","/thumbnail/**","/member/update","/member/delete").hasRole("MEMBER")//계속 로그인하면서 실행확인해야해서 우선 주석처리해놓음
                /* 위에 서술된 패턴 외의 요청은 인증되지 않은 사용자도 요청 허가*/
                .anyRequest().permitAll()
                .and()

                /*로그인 설정*/
                .formLogin()
                .loginPage("/member/login")
                .defaultSuccessUrl("/")
                .failureForwardUrl("/member/loginfail")
                .usernameParameter("memberId")
                .passwordParameter("memberPwd")
                .and()
                /*로그 아웃 설정*/
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/")
                .and()
                /*인증/인가 예외처리 : 인증이 필요하면 로그인 페이지로 이동하므로 인가 처리만 설정*/
                .exceptionHandling()
                .accessDeniedPage("/error/denied")/*인가가되지 않았을때의 처리*/
                .and()
                .build();

    }


                /*사용자 인증을 위해서 사용할 Service 등록 & 비밀번호 인코딩 방식 지정*/
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception{

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(authenticationService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}
