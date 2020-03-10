package com.zhao.mapper;

import com.zhao.pojo.AcItems;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AcItemsMapper {
    int insertAcItem(AcItems acItems);

    List<AcItems> selectAll();

    long selectCount();

    List<AcItems> selectByPage(@Param("pageStart") Integer pageStart, @Param("pageSize") Integer pageSize);

    List<AcItems> selectByParam(@Param("field") String field, @Param("word") String word);

    List<AcItems> selectByParams(@Param("field") String field, @Param("word") String word, @Param("seq") String seq,
                                 @Param("start") int start, @Param("size") int size);

    List<AcItems> sort(@Param("category") String category, @Param("start") int start, @Param("size") int size);

    AcItems selectById(Integer id);

    AcItems selectLastItem();

    int updateById(AcItems acItems);

    int updateStatus(@Param("id") int id,@Param("status")int status);

    int updateRating(@Param("rating") float rating, @Param("acId") int acId);

    AcItems selectLastName(String word);

    List<AcItems> selectByKey(@Param("keyword") String keyword);

    List<AcItems> selectByNames(AcItems acItems);
}
