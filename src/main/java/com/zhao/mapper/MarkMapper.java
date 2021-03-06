package com.zhao.mapper;

import com.zhao.pojo.Mark;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface MarkMapper {
    int insertMark(Mark acMark);

    Mark selectById(@Param("acId") int acId, @Param("uid") int uid);

    List<Mark> selectComments(int acId);

    List<Mark> selectByUid(@Param("uid") int uid, @Param("orderField") String orderField, @Param("orderType") String orderType);

    List<Map<String,Long>> selectRating(int acId);

    int updateMark(Mark acMark);

    int countRating(@Param("acId") int acId);

    float avgRating(@Param("acId") int acId);
}
