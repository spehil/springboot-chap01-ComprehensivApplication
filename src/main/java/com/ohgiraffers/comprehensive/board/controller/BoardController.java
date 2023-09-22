package com.ohgiraffers.comprehensive.board.controller;

import com.ohgiraffers.comprehensive.board.dao.BoardMapper;
import com.ohgiraffers.comprehensive.board.dto.BoardDTO;
import com.ohgiraffers.comprehensive.board.dto.ReplyDTO;
import com.ohgiraffers.comprehensive.board.service.BoardService;
import com.ohgiraffers.comprehensive.member.dto.MemberDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/board")
public class BoardController {
    private  final BoardService boardService;


    public  BoardController(BoardService boardService){
        this.boardService = boardService;//생성자로 의존성주입
    }

    @GetMapping("list")
    public String getBoardList(@RequestParam(defaultValue = "1")int page,
                               @RequestParam(required = false) String searchCondition,
                               @RequestParam(required = false) String searchValue,
                                Model model){
        // @RequestParam(required = false)는 필수값이 아닌 옵셔널이라는 의미이므로 false로 지정
        //false가 아닌데 값이 안넘어오면 400번 에러가 난다.



        log.info("boardList page : {}", page);

        log.info("boardList searchCondition:{}", searchCondition);
        log.info("boardList searchValue : {}", searchValue);

        Map<String, String> searchMap = new HashMap<>();

        searchMap.put("searchCondition", searchCondition);
        searchMap.put("searchValue",searchValue);

         Map<String, Object> boardListAndPaging =  boardService.selectBoardList(searchMap,page);
         model.addAttribute("paging", boardListAndPaging.get("paging"));
         model.addAttribute("boardList", boardListAndPaging.get("boardList"));
        //페에지 목록에 검색기능 +검색어가 있는경우에 페이징처리도 함께해주므로 파라미터에 검색에 대한 페이징값도 넘어가야함.
        return "board/boardList";
    }

    @GetMapping("/detail")
    public String getBoardDetail(@RequestParam Long no, Model model){

        BoardDTO boardDetail =boardService.selectBoardDetail(no);//댓글이 있으면 댓글도 잘 조회되도록 코드작성해놓음
        model.addAttribute("board", boardDetail);

        return "board/boardDetail";
    }
        @PostMapping("registReply")
            public ResponseEntity<String>registReply(@RequestBody ReplyDTO registReply,
                                                     @AuthenticationPrincipal MemberDTO member){
        //JSON이므로 @RequestBody사용해서 받아온다.



            registReply.setWriter(member);
            log.info("registReply : {}",registReply );

            boardService.registReply(registReply);

            return  ResponseEntity.ok("댓글 등록 완료");
}
@GetMapping("/loadReply")
    public ResponseEntity<List<ReplyDTO>> loadReply(ReplyDTO loadReply){

        log.info("loadReply refBoardNo : {}", loadReply.getRefBoardNo());

        List<ReplyDTO> replyList = boardService.loadReply(loadReply);

        log.info("loadReply replyList : {}", replyList);
        return ResponseEntity.ok(replyList);
}

    @PostMapping("/removeReply")
    public ResponseEntity<String> removeReply(@RequestBody ReplyDTO removeReply) {

        log.info("removeReply no : {}", removeReply.getNo());

        boardService.removeReply(removeReply);

        return ResponseEntity.ok("댓글 삭제 완료");

}

@GetMapping("/regist")

    public String getRegistPage(BoardDTO board,@AuthenticationPrincipal MemberDTO member){



    /*boardRegist.html과 BoardMapper.xml을 참조하여 게시글 삽입이 되도록 구현
     * */

    board.setWriter(member);

    log.info("registBoard board : {}", board);

    boardService.registBoard(board);

    return "redirect:/board/list";









}
}
