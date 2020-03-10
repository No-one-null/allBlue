package com.zhao.mapper;

import com.zhao.pojo.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CommentMapper {
    int insertOne(Comment comment);

    Comment selectById(int id);

    List<Comment> selectAll(@Param("tid") int tid);

    int deleteOne(int id);

    int deleteByTid(int tid);
}
