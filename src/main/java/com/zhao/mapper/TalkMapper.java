package com.zhao.mapper;

import com.zhao.pojo.Talk;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TalkMapper {
    int insertTalk(Talk talk);

    long countTalk(int status);

    List<Talk> selectAll(@Param("status") int status,
                         @Param("orderField") String orderField, @Param("orderType") String orderType);

    List<Talk> selectByPage(@Param("pStart") int pStart, @Param("pSize") int pSize, @Param("status") int status,
                            @Param("orderField") String orderField, @Param("orderType") String orderType);

    List<Talk> selectByUid(@Param("uid") int uid,
                           @Param("orderField") String orderField, @Param("orderType") String orderType);

    List<Talk> selectByParams(@Param("field") String field, @Param("keyword") String keyword,
                              @Param("orderField") String orderField, @Param("orderType") String orderType,
                              @Param("pStart") int pStart, @Param("pSize") int pSize, @Param("status") int status);

    Talk selectOne(@Param("tid") int tid);

    int deleteById(int tid);

    int updateStatus(@Param("tid") int tid, @Param("status") int status);
}
