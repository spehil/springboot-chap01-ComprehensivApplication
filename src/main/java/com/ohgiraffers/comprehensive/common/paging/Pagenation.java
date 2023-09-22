package com.ohgiraffers.comprehensive.common.paging;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
@Slf4j
public class Pagenation {

    public  static SelectCriteria getSelectCriteria (int page, int totalCount, int limit, int buttonAmount, Map<String, String> searchMap){

        /*총 페이지 수 계산*/
        int maxPage = (int)Math.ceil((double) totalCount/limit);//실수로 만들고 올림처리-> 페이지수계산시 남은 소숫점의 게시글을 페이지에서 보여주기 위해 작성
        /*페이징 바 시작 숫자*/
        int startPage = (int)(Math.ceil((double)page/ buttonAmount)-1) * buttonAmount + 1;//보여주고싶은 페이징바와 끊어서 보고싶은 페이지수

        /*페이징 바 끝 숫자*/
        int endPage =startPage + buttonAmount -1;

        /*max 페이지가 end 페이지보다 더 작은 경우 end 페이지는 max 페이지이다*/
        if(maxPage < endPage) endPage = maxPage;

        /*마지막 페이지는 0이 될수 없으므로 게시물이 아무것도 존재하지 않으면 max,end는 0이 된다.*/

        if(maxPage ==0 && endPage ==0){

            maxPage = startPage;
            endPage = startPage;
        }
    /*조회 시작행과 마지막 행 계산*/
        int startRow = (page -1) * limit +1; //limit: 한페이지에 보여줄 게시글수
        int endRow = startRow + limit -1;

         return  new SelectCriteria(page, totalCount,limit,buttonAmount, maxPage, startPage,endPage,startRow,endRow,searchMap.get("searchCondition"),searchMap.get("searchValue"));


    }

    public static SelectCriteria getSelectCriteria(int page, int totalCount, int limint, int buttonAmount) {

        return  getSelectCriteria(page, totalCount, limint, buttonAmount, new HashMap<>());
    }
}
