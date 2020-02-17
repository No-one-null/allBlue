package com.zhao.service;

import com.zhao.pojo.AcItems;
import com.zhao.pojo.AcNews;
import com.zhao.pojo.User;
import com.zhao.util.PageInfo;

import java.util.List;

public interface DataService {
    PageInfo showPage(String ac, String pageNumber, String pageSize);

    List findByWord(String table, String keyword);

    List<User> showAll();

    int addAcItems(AcItems acItems);

    List<AcItems> allAcItems();

    AcItems findById(Integer id);

    AcItems findLastOne();

    AcItems findLastOne(String word);

    void updateOne(AcItems acItems);

    void DeleteOne(Integer id);

    Boolean addNews(AcNews acNews);

    AcNews findNewsById(String id);

    Boolean updNews(String id, String status);

    int findNextId(String id);
}
