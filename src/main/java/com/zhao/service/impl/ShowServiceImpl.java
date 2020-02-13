package com.zhao.service.impl;

import com.zhao.mapper.AcItemsMapper;
import com.zhao.mapper.MarkMapper;
import com.zhao.mapper.UserMapper;
import com.zhao.pojo.AcItems;
import com.zhao.pojo.Mark;
import com.zhao.pojo.PageInfo;
import com.zhao.service.ShowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ShowServiceImpl implements ShowService {
    @Resource
    private AcItemsMapper acItemsMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private MarkMapper markMapper;

    /**
     * 分页
     *
     * @param pageNumber 页数
     * @param pageSize   每页显示数量
     * @return Page对象
     */
    @Override
    public PageInfo showPage(String tbName, String pageNumber, String pageSize) {
        int pSize = 10;
        if (pageSize != null && !pageSize.equals("")) {
            pSize = Integer.parseInt(pageSize);
        }
        int pNum = 1;
        if (pageNumber != null && !pageNumber.equals("")) {
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
        } else {
            pageInfo.setList(acItemsMapper.selectByPage(pageStart, pSize));
            count = acItemsMapper.selectCount();
        }
        pageInfo.setCount(count);
        pageInfo.setTotal(count % pSize == 0 ? count / pSize : count / pSize + 1);
        return pageInfo;
    }

    @Override
    public AcItems findById(String id) {
        return acItemsMapper.selectById(Integer.valueOf(id));
    }

    @Override
    public List<AcItems> findByWord(String field, String word) {
        return acItemsMapper.selectByParam(field, word);
    }

    @Override
    public List<AcItems> findByParam(String field, String word, String seq, int start, int end) {
        return acItemsMapper.selectByParams(field, word, seq, 0, end);
    }

    @Override
    public List<AcItems> Sort(String field, String word, String sort, String seq, int start, int end) {
        return acItemsMapper.sortByParams(field,word,sort,seq,start,end);
    }

    @Transactional
    @Override
    public Boolean addMark(Mark mark) {
        Mark mark1 = markMapper.selectById(mark.getAcId(), mark.getUid());
        int flag = 0;
        if (mark1 == null) {
            flag = markMapper.insertMark(mark);
        }
        int flag2 = markMapper.updateMark(mark);
        if (flag != 0 || flag2 != 0) {
            return true;
        }
        return false;
    }

    @Override
    public Mark findMarkOne(int uid, int acId) {
        return markMapper.selectById(uid, acId);
    }

    @Transactional
    @Override
    public float calRating(String acId, float rating) {
        float newRating = markMapper.avgRating(Integer.valueOf(acId));
        float RealRating = (float) Math.round(newRating * 10) / 10;
        if (rating == RealRating) {
            return rating;
        } else {
            acItemsMapper.updateRating(RealRating, Integer.valueOf(acId));
            return RealRating;
        }
    }

    @Override
    public int sumRating(String acId) {
        return markMapper.countRating(Integer.valueOf(acId));
    }

    @Override
    public List<Mark> allComments(String acId) {
        return markMapper.selectComments(Integer.valueOf(acId));
    }
}
