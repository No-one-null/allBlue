package com.zhao.service;

import com.zhao.pojo.AcItems;

import java.util.List;

public interface DataService {
    int addAcItems(AcItems acItems);

    List<AcItems> allAcItems();

    List<AcItems> findByWord(String keyword);

    AcItems findById(Integer id);

    AcItems findLastOne();

    AcItems findLastOne(String word);

    void updateOne(AcItems acItems);

    void DeleteOne(Integer id);
}
