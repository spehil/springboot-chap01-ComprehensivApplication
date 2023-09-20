package com.ohgiraffers.comprehensive.member.controller;

import com.ohgiraffers.comprehensive.common.exception.member.MemberModifyException;
import com.ohgiraffers.comprehensive.common.exception.member.MemberRegistException;
import com.ohgiraffers.comprehensive.common.exception.member.MemberRemoveException;
import com.ohgiraffers.comprehensive.member.dto.MemberDTO;
import com.ohgiraffers.comprehensive.member.service.AuthenticationService;
import com.ohgiraffers.comprehensive.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Slf4j
@Controller
@RequestMapping("/member")

public class MemberController {
    private final MemberService memberService;

    private final AuthenticationService authenticationService;
    private final MessageSourceAccessor messageSourceAccessor;//의존성주입

    private  final PasswordEncoder passwordEncoder;
    public MemberController(MessageSourceAccessor messageSourceAccessor, MemberService memberService, PasswordEncoder passwordEncoder, AuthenticationService authenticationService) {
        this.memberService = memberService;
        this. authenticationService = authenticationService;
        this.messageSourceAccessor = messageSourceAccessor;
        this.passwordEncoder = passwordEncoder;

    }

    @GetMapping("/login")
    public void loginPage() {
    }

    /*로그인 실패시 */
    @PostMapping("/loginfail")
    public String loginFailed(RedirectAttributes rttr) {
        rttr.addFlashAttribute("message", messageSourceAccessor.getMessage("error.login"));
        return "redirect:/member/login";

    }

    /*회원 가입 페이지 이동*/
    @GetMapping("/regist")
    public void registPage(){}

    /*아이디 중복체크 : 비동기 통신
    * ResponseEntity : 사용자의 HttpRequest에 대한 응답 데이터를 포함하는 클래스
    * (HttpStatus, HttpHeaders, HttpBody를 포함한다.)
    * 규약에 맞는 HttpResponse를 반환하는데 사용 목적이 있다.*/
    @PostMapping("/idDupCheck")
    public ResponseEntity<String> checkDuplication(@RequestBody MemberDTO member){
        //@RequestBody ->json형태의 문자열이 자바형식으로 자동으로 바뀜
        log.info("Request Check ID : {}" , member.getMemberId());
        String result = "사용 가능한 아이디입니다.";
        if(memberService.selectMemberById(member.getMemberId())){
            result = "중복된 아이디가 존재합니다.";
        }
        return ResponseEntity.ok(result);
    }

    /*회원 가입 */

    @PostMapping("/regist")
    public String registMember(MemberDTO member,String zipCode, String address1, String address2,RedirectAttributes rttr) throws MemberRegistException {//@ModelAttrubute 생략가능해서 생략함. @RequestParam도 생략가능해서 생략함

        //데이터 가공
        String address = zipCode + "$" + address2;
        member.setAddress(address);
        member.setMemberPwd(passwordEncoder.encode(member.getPassword()));


        log.info("Request regist member : {}", member);

       //회원 등록 동작
        memberService.registMember(member);
        //redirect처리해줘야한다. 회원가입상황이 다시 발생하지 못하도록 새로운 상황의 redirect로 보내준다.
        rttr.addFlashAttribute("message", messageSourceAccessor.getMessage("member.regist"));

        return "redirect:/";
    }

    /*회원 정보 화면 이동*/
    @GetMapping("/update")

    public void modifyPage(@AuthenticationPrincipal MemberDTO member, Model model){

        String[] address = member.getAddress().split("\\$");//$를 이스케이프로 \\작성해줌
        model.addAttribute("address", address);


    }

    /*회원 정보 수정*/
    @PostMapping("/update")
    public String modifyMember(MemberDTO modifyMember,String zipCode, String address1, String address2,
                                            @AuthenticationPrincipal MemberDTO loginMember, RedirectAttributes rttr)throws MemberModifyException {//누구인지를 알아야 어떤행을 수행할지를 알수 있으므로 누구인지는

        String address = zipCode + "$" + address1 + "$" + address2;
        modifyMember.setAddress(address);
        modifyMember.setMemberNo(loginMember.getMemberNo());

        log.info("modifyMember request Member : {}" , modifyMember);

        memberService.modifyMember(modifyMember);

        /*로그인시 저장된 Authentication 객체를 변경된 정보로 교체한다.*/
        SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(loginMember.getMemberId()));

        rttr.addFlashAttribute("message", messageSourceAccessor.getMessage("member.modify"));
        return "redirect:/";
    }
    protected Authentication createNewAuthentication(String memberId){

        UserDetails newPrincipal = authenticationService.loadUserByUsername(memberId);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newPrincipal, newPrincipal.getPassword(),newPrincipal.getAuthorities());
        return newAuth;
    }

    /*회원 탈퇴 */
    /*로그인 된 멤버를 정보를 전달하며 memberService.removeMember(member)메소드 호출
    * SecurityContextHolder.clearContext();=> 메소드를 호출하려면 authentication 정보가 지워짐
      탈퇴완료
      index화면으로 이동
*/
    @GetMapping("/delete")

    public String deleteMember(@AuthenticationPrincipal MemberDTO member, RedirectAttributes rttr) throws MemberRemoveException {

        log.info("login member : {}", member);
         memberService.removeMember(member);

         SecurityContextHolder.clearContext();
        rttr.addFlashAttribute("message", messageSourceAccessor.getMessage("member.delete"));
        return "redirect:/";

    }
}