package com.zhao.mapper;

import com.zhao.pojo.MsgContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MsgContentMapper {
    int insertOne(MsgContent msgContent);

    MsgContent selectById(int id);

    List<MsgContent> selectByTypeAndMax(@Param("type") String type,@Param("max") long max);
}
