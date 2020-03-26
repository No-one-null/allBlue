package com.zhao.service;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.AcNews;
import com.zhao.pojo.Talk;
import com.zhao.pojo.User;
import com.zhao.util.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    AcItems findById(String id);

    String updateOne(AcItems acItems, MultipartFile file) throws IOException;

    boolean DeleteOne(int id) throws IOException;

    String addNews(AcNews acNews,String[] filenames) throws IOException;

    AcNews findNewsById(String id);

    String editNews(String id, String content, String type, String status,String[] filenames) throws IOException;

    String checkTopic(String type, int tid, String deal, int uid);

    Map<String, Object> findTalk(String type, int tid);

    Map<String, Object> showUserAndRoles(String uid);

    String[] uploadImg(MultipartFile[] file, HttpServletRequest request) throws IOException;
}
