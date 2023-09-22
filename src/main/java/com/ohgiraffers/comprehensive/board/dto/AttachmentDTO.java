package com.ohgiraffers.comprehensive.board.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttachmentDTO {

    private Long no;
    private Long refBoardNo; //썸네일이 등록된 게시글 번호

    private String originalName;

    private  String savedName;

    private  String savePath;

    private  String fileType; //썸네일인거랑 아닌것 구분

    private  String thumbnailPath;

    private String status;
}
