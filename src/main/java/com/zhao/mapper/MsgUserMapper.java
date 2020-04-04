package com.zhao.mapper;

import com.zhao.pojo.MsgUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface MsgUserMapper {
    @Select("SELECT COUNT(mid) FROM msg_user WHERE uid=#{0} AND status=#{1} AND type=#{2}")
    Long countMsg(int uid, int status, String type);

    @Update("UPDATE msg_user SET status=1 WHERE uid=#{0} AND type=#{1}")
    Integer updateMsg(int uid, String type);

    @Select("SELECT MAX(mid) FROM msg_user WHERE uid=#{0} AND type=#{1}")
    Long selectMaxMidByUidAndType(int uid,String type);

    Integer insertOne(MsgUser msgUser);

    Long insertBatch(List<MsgUser> msgUsers);

    List<MsgUser> selectByStatus(int uid, int status);

    List<MsgUser> selectByType(@Param("uid") int uid, @Param("status") int status, @Param("type") String type);
}
