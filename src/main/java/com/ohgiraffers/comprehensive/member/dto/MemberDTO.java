package com.ohgiraffers.comprehensive.member.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@ToString
public class MemberDTO implements UserDetails {//implements UserDetails 반드시 구현해야할 오버라이딩 메소드들이있음.
    private Long memberNo;
    private String memberId;
    private String memberPwd;
    private String nickname;
    private String phone;

    private String email;
    private String address;
    private Date enrollDate;
    private  String memberStatus;
    private List<MemberRoleDTO> memberRoleList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> roles = new HashSet<>();
        for(MemberRoleDTO role : memberRoleList){
            roles.add(new SimpleGrantedAuthority(role.getAuthority().getName()));
        }
        return roles;
    }

    @Override
    public String getPassword() {
        return memberPwd;
    }

    @Override
    public String getUsername() {
        return memberId;
    }

    @Override
    public boolean isAccountNonExpired() {//계정이 만료되지 않은건지 물어보는거

        return true; //만료되지 않았다. false를 해놓으면 아이디 비밀번호를 맞게 작성해도 로그인시도 할떄 로그인이 안됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
