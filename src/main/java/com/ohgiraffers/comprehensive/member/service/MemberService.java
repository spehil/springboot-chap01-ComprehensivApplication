package com.ohgiraffers.comprehensive.member.service;

import com.ohgiraffers.comprehensive.common.exception.member.MemberModifyException;
import com.ohgiraffers.comprehensive.common.exception.member.MemberRegistException;
import com.ohgiraffers.comprehensive.common.exception.member.MemberRemoveException;
import com.ohgiraffers.comprehensive.member.dao.MemberMapper;
import com.ohgiraffers.comprehensive.member.dto.MemberDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberMapper memberMapper;
    public MemberService(MemberMapper memberMapper){
        this.memberMapper = memberMapper;
    }

    public boolean selectMemberById(String memberId) {

        String result = memberMapper.selectMemberById(memberId);
            return result != null;
        }
@Transactional
    public void registMember(MemberDTO member) throws MemberRegistException {
        int result1= memberMapper.insertMember(member);

        int result2 = memberMapper.insertMemberRole();

        //위에 두개의 메소드중에 하나라도 잘못된경우 우리가 정의한 Exception으로 Handling하고 싶어서 코드 작성

    if(! (result1 >0 && result2 >0)) throw new MemberRegistException("회원가입에 실패하였습니다.");


    }

    @Transactional
    public void modifyMember(MemberDTO modifyMember) throws MemberModifyException {
        int result = memberMapper.updateMember(modifyMember);

        if(! (result >0)) throw  new MemberModifyException("회원정보 수정이 실패하였습니다.");

    }


    /*회원 탈퇴
    * memberMapper.deleteMember(member)호출하여 회원 탈퇴 처리 */
    /*회원 탈퇴 수행되지 않았을 경우 MemberRemoveException 발생*/
@Transactional
    public void removeMember(MemberDTO member)throws MemberRemoveException {

        int result = memberMapper.deleteMember(member);

        if(! (result >0)) {
            throw  new MemberRemoveException("회원탈퇴에 실패하였습니다.");}


    }

}

