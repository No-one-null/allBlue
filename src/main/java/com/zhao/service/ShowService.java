package com.zhao.service;

import com.zhao.pojo.*;
import com.zhao.util.PageInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ShowService {

    AcItems findById(String id);

    PageInfo showAcItems(String path, String orderField, String orderType, String page);

    List<AcItems> sort(String category, int start, int end);

    Boolean addMark(Mark mark);

    Mark findMarkOne(int uid, int acId);

    float calRating(String acId, float rating);

    int sumRating(String acId);

    List<Mark> allComments(String acId);

    PageInfo showPage(String path, String type, String pNum, String pSize);

    AcNews showNews(String nid);

    int addTalk(Talk talk);

    List<Talk> showAllTalk();

    Talk showTalk(String tid, HttpServletRequest request);

    boolean addComment(Comment comment);

    List<Comment> showComments(String tid);

    boolean delComment(String id);

    String removeTalk(String tid) throws IOException;

    String sendComplaint(Complaint complaint);

    Map<String, Object> showUserInfo(String uid, String page);

    String updateUser(User user, MultipartFile file) throws IOException;

    User showUser(int uid);

    List<?> findByWord(String tb,String conditions, String word, String order);
}
