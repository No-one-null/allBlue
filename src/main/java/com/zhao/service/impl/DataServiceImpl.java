package com.zhao.service.impl;

import com.zhao.mapper.AcItemsMapper;
import com.zhao.mapper.AcNewsMapper;
import com.zhao.mapper.UserMapper;
import com.zhao.pojo.AcItems;
import com.zhao.pojo.AcNews;
import com.zhao.pojo.User;
import com.zhao.service.DataService;
import com.zhao.util.PageInfo;
import com.zhao.util.CommonUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class DataServiceImpl implements DataService {
    @Resource
    private AcItemsMapper acItemsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private AcNewsMapper acNewsMapper;

    /**
     * 分页
     *
     * @param pageNumber 页数
     * @param pageSize   每页显示数量
     * @return Page对象
     */
    @Override
    public PageInfo showPage(String tbName, String pageNumber, String pageSize) {
        CommonUtil v = new CommonUtil();
        int pSize = 10;
        if (v.isNumber1(pageSize)) {
            pSize = Integer.parseInt(pageSize);
        }
        int pNum = 1;
        if (v.isNumber1(pageNumber)) {
            pNum = Integer.parseInt(pageNumber);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pNum);
        pageInfo.setPageSize(pSize);
        int pageStart = pSize * (pNum - 1);
        long count = 0;
        if (tbName.equalsIgnoreCase("user")) {
            pageInfo.setList(userMapper.selectByPage(pageStart, pSize));
            count = userMapper.selectCount();
        }
        if (tbName.equalsIgnoreCase("ac")) {
            pageInfo.setList(acItemsMapper.selectByPage(pageStart, pSize));
            count = acItemsMapper.selectCount();
        }
        if (tbName.equalsIgnoreCase("news")) {
            pageInfo.setList(acNewsMapper.selectByPage(pageStart, pSize));
            count = acNewsMapper.CountAll();
        }
        pageInfo.setCount(count);
        pageInfo.setTotal(count % pSize == 0 ? count / pSize : count / pSize + 1);
        return pageInfo;
    }

    @Override
    public int addAcItems(AcItems acItems) {
        return this.acItemsMapper.insertAcItem(acItems);
    }

    @Override
    public List<User> showAll() {
        return this.userMapper.showAll();
    }

    @Override
    public List<AcItems> allAcItems() {
        return this.acItemsMapper.selectAll();
    }

    @Override
    public List findByWord(String table, String keyword) {
        if ((keyword.equals("_")) || (keyword.equals("%"))) {
            keyword = "\\" + keyword;
        }
        if (table.equals("ac")) {
            return acItemsMapper.selectByKey(keyword);
        }
        if (table.equals("news")) {
            return acNewsMapper.selectByKeyword(keyword);
        }
        return null;
    }

    @Override
    public AcItems findById(Integer id) {
        return acItemsMapper.selectById(id);
    }

    @Override
    public AcItems findLastOne() {
        return acItemsMapper.selectLastItem();
    }

    @Override
    public AcItems findLastOne(String word) {
        return acItemsMapper.selectLastName(word);
    }

    @Transactional
    @Override
    public void updateOne(AcItems AcItems) {
        acItemsMapper.updateById(AcItems);
    }

    @Transactional
    @Override
    public void DeleteOne(Integer id) {
        acItemsMapper.updateDelete(id);
    }

    @Transactional
    @Override
    public Boolean addNews(AcNews acNews) {
        acNews.setNewsDate(new Date());
        int flag = acNewsMapper.insertAcNews(acNews);
        if (flag != 0) {
            return true;
        }
        return false;
    }

    @Override
    public AcNews findNewsById(String id) {
        if (id != null) {
            return acNewsMapper.selectOneById(Integer.valueOf(id));
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public Boolean updNews(String id, String status) {
        if (id != null && status != null) {
            int flag = acNewsMapper.updateStatus(Integer.valueOf(id), Integer.valueOf(status));
            if (flag != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int findNextId(String id) {
        int next = Integer.valueOf(id);
        int last = acNewsMapper.selectLastId();
        if (next >= last) {
            next = acNewsMapper.selectFirstId();
        } else {
            next = acNewsMapper.selectNextId(next);
        }
        return next;
    }
}
