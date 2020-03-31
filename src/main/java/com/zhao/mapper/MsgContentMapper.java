package com.zhao.mapper;

import com.zhao.pojo.MsgContent;

public interface MsgContentMapper {
    int insertOne(MsgContent msgContent);

    MsgContent selectById(int id);
}
