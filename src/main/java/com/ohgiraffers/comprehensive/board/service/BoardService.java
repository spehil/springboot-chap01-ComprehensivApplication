package com.ohgiraffers.comprehensive.board.service;

import com.ohgiraffers.comprehensive.board.dao.BoardMapper;
import com.ohgiraffers.comprehensive.board.dto.BoardDTO;
import com.ohgiraffers.comprehensive.board.dto.ReplyDTO;
import com.ohgiraffers.comprehensive.common.paging.Pagenation;
import com.ohgiraffers.comprehensive.common.paging.SelectCriteria;
import com.ohgiraffers.comprehensive.board.dto.AttachmentDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
@Transactional//트랜젝션 어노테이션 메소드위랑 클래스위에 작성가능
public class BoardService {

    private final BoardMapper boardMapper;
    public  BoardService(BoardMapper boardMapper){
        this.boardMapper = boardMapper;//생성자로 의존성주입
    }

    public Map<String, Object> selectBoardList(Map<String, String> searchMap, int page) {

        /*1. 전체 게시글 수 확인(검색어가 있는경우 포함) => 페이징처리를 위해*/

        int totalCount = boardMapper.selectTotalCount(searchMap);//검색어에 대한 조회요청
        log.info("boardList totalCount :{}", totalCount);


       /*2. 페이징 처리와 연관 된 값을 계산하여 SelectCritieria 타입의 객체를 담는다.*/
        int limit = 10;//한 페이지에 보여줄 게시물의 수
        int buttonAmount = 5;//한 번에 보여질 페이징 버튼의 수
        SelectCriteria selectCriteria = Pagenation.getSelectCriteria(page, totalCount, limit, buttonAmount, searchMap);
        log.info("boardList selectCriteria:{}", selectCriteria);

        /*3. 요청 페이지와 검색 기준에 맞는 게시글을 조회해온다.*/
        List<BoardDTO> boardList = boardMapper.selectBoardList(selectCriteria);
       log.info("boardList : {}",boardList);

       Map<String, Object>boardListAndPaging = new HashMap<>();// SelectCriteria selectCriteria,  List<BoardDTO> boardList
       boardListAndPaging.put("paging", selectCriteria);
       boardListAndPaging.put("boardList", boardList);
       return boardListAndPaging;

    }


    public BoardDTO selectBoardDetail(Long no) {
        //게시글 데이터 조회 +조회수


        /*조회수 증가 로직*/
        boardMapper.incrementBoardCount(no);

        /*게시글 상세 내용 조회후 리턴*/
        return  boardMapper.selectBoardDetail(no);

    }

    public void registReply(ReplyDTO registReply) {
        boardMapper.insertReply(registReply);
    }

    public List<ReplyDTO> loadReply(ReplyDTO loadReply) {
        return boardMapper.selectReplyList(loadReply);
    }


    public void removeReply(ReplyDTO removeReply) {

        boardMapper.deleteReply(removeReply);
    }


    public void registBoard(BoardDTO board) {
        boardMapper.insertBoard(board);
    }

    public void registThumbnail(BoardDTO board) {
        /*Board 테이블에 데이터 저장*/
        boardMapper.insertThumbnailContent(board);

        /*Attachment 테이블에 데이터 저장(첨부파일 개수 만큼)*/
        for(AttachmentDTO attachment : board.getAttachmentList()){// board.getAttachmentList():첨부된 파일수
            boardMapper.insertAttachment(attachment);
        }


    }

    public Map<String, Object> selectThumbnailList(int page) {
        //전체 컨텐츠 갯수
        int totalCount = boardMapper.selectThumbnailTotalCount();
        log.info("thumbnail total : {}" , totalCount);

        int limint = 9;// 9개씩 보여지도록 작성

        int buttonAmount = 5; //버튼 갯수 5개


        SelectCriteria selectCreteria = Pagenation.getSelectCriteria(page, totalCount,limint, buttonAmount);
        log.info("thumbnail selectCriteria : {} ", selectCreteria);

        List<BoardDTO> thumbnailList = boardMapper.selectThumbnailBoardList(selectCreteria);

        log.info("thumbnail thumbnailList : {} ", thumbnailList);

        Map<String, Object>thumbnailListAndPaging = new HashMap<>();

        thumbnailListAndPaging.put("paging", selectCreteria);
        thumbnailListAndPaging.put("thumbnailList", thumbnailList);

        return thumbnailListAndPaging;


    }

    public BoardDTO selectThumbnailDetail(Long no) {

        /*조회수 증가 로직 호출*/
        boardMapper.incrementBoardCount(no);

        /*사진 게시글 조회후 반환*/

        return boardMapper.selectThumbnailBoardDetail(no);


    }
}
