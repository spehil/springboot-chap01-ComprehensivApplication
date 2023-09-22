package com.ohgiraffers.comprehensive.board.dao;

import com.ohgiraffers.comprehensive.board.dto.BoardDTO;
import com.ohgiraffers.comprehensive.board.dto.ReplyDTO;
import com.ohgiraffers.comprehensive.common.paging.SelectCriteria;
import com.ohgiraffers.comprehensive.board.dto.AttachmentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface  BoardMapper {


        int selectTotalCount(Map<String, String> searchMap);

        List<BoardDTO> selectBoardList(SelectCriteria selectCriteria);

        void incrementBoardCount(Long no);

        BoardDTO selectBoardDetail(Long no);

        void insertReply(ReplyDTO registReply);

        List<ReplyDTO> selectReplyList(ReplyDTO loadReply);

        void deleteReply(ReplyDTO removeReply);

        void insertBoard(BoardDTO board);

        void insertThumbnailContent(BoardDTO board);

        void insertAttachment(AttachmentDTO attachment);

        int selectThumbnailTotalCount();

        List<BoardDTO> selectThumbnailBoardList(SelectCriteria selectCreteria);

        BoardDTO selectThumbnailBoardDetail(Long no);
}
