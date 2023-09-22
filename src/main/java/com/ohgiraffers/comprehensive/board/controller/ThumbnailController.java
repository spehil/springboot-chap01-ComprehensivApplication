package com.ohgiraffers.comprehensive.board.controller;

import com.ohgiraffers.comprehensive.board.dto.BoardDTO;
import com.ohgiraffers.comprehensive.board.service.BoardService;
import com.ohgiraffers.comprehensive.board.dto.AttachmentDTO;
import com.ohgiraffers.comprehensive.member.dto.MemberDTO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Controller
@RequestMapping("/thumbnail")
public class ThumbnailController {



    @Value("${image.image-dir}")//springframework annotatin으로 import
    private String IMAGE_DIR;

    private final BoardService boardService;//의존성주입

    public ThumbnailController(BoardService boardService){
        this.boardService = boardService;//생성자 의존성주입
    }


    @GetMapping("/regist")
    public String getRegistPage() {

        return "/thumbnail/thumbnailRegist";

    }

    @PostMapping("regist")
    public String registThumbnail(BoardDTO board, List<MultipartFile> attachImage,
                                  @AuthenticationPrincipal MemberDTO member) {//title,body를 전달하기 위해 BoardDTO타입으로 전달

        log.info("thumbnail board request : {}", board);
        log.info("thumb attachImage request : {}", attachImage);

        String fileUploadDir = IMAGE_DIR + "original";
        String thumbnailDir = IMAGE_DIR + "thumbnail";

        File dir1 = new File(fileUploadDir);
        File dir2 = new File(thumbnailDir);


        /*디렉토리가 없을 경우 생성한다.*/
        if (!dir1.exists() || !dir2.exists()) {
            dir1.mkdirs();
            dir2.mkdirs();
        }



        /*업로드 파일에 대한 정보를 담을 리스트 */
        List<AttachmentDTO> attachmentList = new ArrayList<>();



        /*서버의 설정 디렉토리에 파일 저장*/


        try {

            for (int i = 0; i < attachImage.size(); i++) {//attachImage.size():4개
                //첨부파일이 길제로 존재하는 경우에만 로직 수행
                if (attachImage.get(i).getSize() > 0) {
                    //파일명을 rename해서 저장
                    String origianlFileName = attachImage.get(i).getOriginalFilename();//파일객체의 업로드명
                    log.info("originalFileName : {} ", origianlFileName);

                    String ext = origianlFileName.substring(origianlFileName.lastIndexOf("."));//확장자명 추출
                    String savedFileName = UUID.randomUUID() + ext;//원래파일명이 아닌 고유한 파일명을 만든후 + 추출한 확장자명을 붙여준다.
                    log.info("savedFileName : {}", savedFileName);


                    log.info("fileUploadDir : {}", fileUploadDir);
                    log.info("thumbnailDir : {}", thumbnailDir);


                    /*서버의 설정 디렉토리 파일에 저장*/
                    attachImage.get(i).transferTo(new File(fileUploadDir + "/" + savedFileName));//transferTo:전달된 멀티파일 리스트를 원하는 디렉토리로 저징이 가능하다. 파일입출력이므로 exception발생할수 있으므로 try/catch

                    /*DB에 저장할 파일의 정보 처리 */
                    AttachmentDTO fileInfo = new AttachmentDTO();
                    fileInfo.setOriginalName(origianlFileName);//
                    fileInfo.setSavedName(savedFileName);
                    fileInfo.setSavePath("/upload/original/");

                    if(i ==0){
                        fileInfo.setFileType("TITLE");
                        /*대표 사진에 대한 썸네일을 가공해서 저장한다.*/
                        Thumbnails.of(fileUploadDir + "/" + savedFileName).size(300,300)//원본파일
                                .toFile(thumbnailDir + "/thumbnail_"+savedFileName);//원하는 경로에 수정한이름으로 저장
                        fileInfo.setThumbnailPath("/upload/thumbnail/thumbnail_" + savedFileName);
                    }else {
                        fileInfo.setFileType("BODY");
                    }

                    attachmentList.add(fileInfo);

                }

            }
        } catch (IOException e) {
           e.printStackTrace();
        }

        log.info("attachList : {}" , attachmentList);

        board.setAttachmentList(attachmentList);
        board.setWriter(member);

        boardService.registThumbnail(board);//필드에 boardService 의존성 주입 작성해준다.

        return "redirect:/thumbnail/list";//썸네일 목록으로 보낸다.

    }

    @GetMapping("/list")
    public String selectThumbnailList(@RequestParam(defaultValue = "1")int page, Model model) {

        Map<String, Object> thumbnailListAndPaging = boardService.selectThumbnailList(page);
        model.addAttribute("paging", thumbnailListAndPaging.get("paging"));
        model.addAttribute("thumbnailList",  thumbnailListAndPaging.get("thumbnailList"));



        return "thumbnail/thumbnailList";
    }
    @GetMapping("/detail")
    public String SelectThumbnailDetail(Long no, Model model){

        log.info("thumbnail no {} :" , no);

        BoardDTO thumbnail = boardService.selectThumbnailDetail(no);
        log.info("thumbnail  {} :" , thumbnail);

        model.addAttribute("thumbnail", thumbnail);
        return "thumbnail/thumbnailDetail";
    }
}
