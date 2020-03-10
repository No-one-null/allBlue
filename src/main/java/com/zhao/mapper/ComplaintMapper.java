package com.zhao.mapper;

import com.zhao.pojo.Complaint;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ComplaintMapper {
    int insertOne(Complaint complaint);

    List<Complaint> selByUidTid(@Param("uid") int uid, @Param("tid") int tid, @Param("type") String type);

    long countAll();

    List<Complaint> selectByPage(@Param("pageStart") int pageStart, @Param("pageSize") int pageSize);

    List<Complaint> selectAndOder(@Param("filed") String filed, @Param("word") String word);

    int countByTalk(int tid);

    int updateStatus(@Param("status") int num, @Param("tid") int tid,
                     @Param("type") String type, @Param("uid") int uid);
}
