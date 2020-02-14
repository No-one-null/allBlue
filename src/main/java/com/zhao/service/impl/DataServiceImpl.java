package com.zhao.service.impl;

import com.zhao.mapper.AcItemsMapper;
import com.zhao.pojo.AcItems;
import com.zhao.service.DataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {
    @Resource
    private AcItemsMapper AcItemsMapper;

    @Override
    public int addAcItems(AcItems acItems) {
        return this.AcItemsMapper.insertAcItem(acItems);
    }

    @Override
    public List<AcItems> allAcItems() {
        return this.AcItemsMapper.selectAll();
    }

    @Override
    public List<AcItems> findByWord(String keyword) {
        if ((keyword.equalsIgnoreCase("_")) || (keyword.equalsIgnoreCase("%"))) {
            keyword = "\\" + keyword;
        }
        return AcItemsMapper.selectByKey(keyword);
    }

    @Override
    public AcItems findById(Integer id) {
        return AcItemsMapper.selectById(id);
    }

    @Override
    public AcItems findLastOne() {
        return AcItemsMapper.selectLastItem();
    }

    @Override
    public AcItems findLastOne(String word) {
        return AcItemsMapper.selectLastName(word);
    }

    @Transactional
    @Override
    public void updateOne(AcItems AcItems) {
        AcItemsMapper.updateById(AcItems);
    }

    @Transactional
    @Override
    public void DeleteOne(Integer id) {
        AcItemsMapper.updateDelete(id);
    }

}
