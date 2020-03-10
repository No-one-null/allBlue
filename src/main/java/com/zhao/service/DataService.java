package com.zhao.service;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.AcNews;
import com.zhao.pojo.Talk;
import com.zhao.pojo.User;
import com.zhao.util.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataService {
    PageInfo showPage(String ac, String pageNumber, String pageSize);

    List<?> findByWord(String table, String keyword);

    List<User> showAll();

    String addAcItems(MultipartFile file, AcItems acItems) throws IOException;

    int addAcItems(AcItems acItems);

    List<AcItems> allAcItems();

    AcItems findById(Integer id);

    String updateOne(AcItems acItems,MultipartFile file) throws IOException;

    boolean DeleteOne(int id) throws IOException;

    Boolean addNews(AcNews acNews);

    AcNews findNewsById(String id);

    Boolean updNews(String id, String status);

    String editNews(String id,String content);

    String checkTopic(String type, int tid, String deal, int uid);

    Map<String, Object> findTalk(String type, int tid);
}
