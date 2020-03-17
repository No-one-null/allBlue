package com.zhao.mapper;

import com.zhao.pojo.AcNews;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AcNewsMapper {
    int insertAcNews(AcNews acNews);

    AcNews selectOneById(int id);

    AcNews selectById(int id);

    List<AcNews> selectByPage(int start, int end);

    int updateContent(@Param("id") int id, @Param("content") String content,
                      @Param("type") String type, @Param("status") int status);

    long CountAll();

    List<AcNews> selectByKeyword(@Param("keyword") String keyword);

    List<AcNews> selectByParams(@Param("start") int start, @Param("size") int size, @Param("type") String type);

//    List<AcNews> selectByParams(@Param("start") int start,@Param("size")int size,
//                                @Param("field")String field,@Param("keyword")String keyword);

    long countNotAll(@Param("type") String type);
}
