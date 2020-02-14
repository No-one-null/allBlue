package com.zhao.service;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.Mark;
import com.zhao.pojo.PageInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShowService {
    PageInfo showPage(String ac, String pageNumber, String pageSize);

    AcItems findById(String id);

    List<AcItems> findByWord(String field, String word);

    List<AcItems> findByParam(String field, String word, String seq, int start, int end);

    List<AcItems> sort(String category,int start,int end);

    Boolean addMark(Mark mark);

    Mark findMarkOne(int uid, int acId);

    float calRating(String acId, float rating);

    int sumRating(String acId);

    List<Mark> allComments(String acId);
}
