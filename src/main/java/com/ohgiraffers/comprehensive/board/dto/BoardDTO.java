package com.ohgiraffers.comprehensive.board.dto;

import com.ohgiraffers.comprehensive.member.dto.MemberDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class BoardDTO {

    private  Long no;
    private  Integer type;//일반게시판인지 사진 게시판인지 구분

    private Long categoryCode;

    private CategoryDTO category;
    private String title;

    private  String body;

    private  Long writerMemberNo;

    private MemberDTO writer;

    private int count;

    private Date createdDate;

    private  Date modifiedDate;

    private String status;

    private List<ReplyDTO> replyList;//하나의 게시글에 여러개의 댓글이 달릴수 있으므로 List타입으로 작성

    private List<AttachmentDTO> attachmentList;

}
