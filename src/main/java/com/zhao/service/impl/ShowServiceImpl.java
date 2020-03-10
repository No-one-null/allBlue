package com.zhao.service.impl;

import com.zhao.mapper.*;
import com.zhao.pojo.*;
import com.zhao.util.PageInfo;
import com.zhao.service.ShowService;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.zhao.util.CommonUtil.*;
import static com.zhao.util.Constant.*;

@Service
public class ShowServiceImpl implements ShowService {
    @Resource
    private AcItemsMapper acItemsMapper;
    @Resource
    private MarkMapper markMapper;
    @Resource
    private AcNewsMapper acNewsMapper;
    @Resource
    private TalkMapper talkMapper;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ComplaintMapper complaintMapper;

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
    public List<AcItems> sort(String category, int start, int end) {
        return acItemsMapper.sort(category, start, end);
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
        return flag != 0 || flag2 != 0;
    }

    @Override
    public Mark findMarkOne(int uid, int acId) {
        return markMapper.selectById(uid, acId);
    }

    @Transactional
    @Override
    public float calRating(String acId, float rating) {
        float newRating = markMapper.avgRating(Integer.parseInt(acId));
        float RealRating = (float) Math.round(newRating * 10) / 10;
        if (rating == RealRating) {
            return rating;
        } else {
            int result=acItemsMapper.updateRating(RealRating, Integer.parseInt(acId));
            return RealRating;
        }
    }

    @Override
    public int sumRating(String acId) {
        return markMapper.countRating(Integer.parseInt(acId));
    }

    @Override
    public List<Mark> allComments(String acId) {
        return markMapper.selectComments(Integer.parseInt(acId));
    }

    @Override
    public PageInfo showPage(String path, String type, String pageNumber, String pageSize) {
        int pNum = 1;
        if (isNumber(pageNumber)) {
            pNum = Integer.parseInt(pageNumber);
        }
        int pSize = 10;
        if (isNumber(pageSize)) {
            pSize = Integer.parseInt(pageSize);
        }
        long count = 0;
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNumber(pNum);
        pageInfo.setPageSize(pSize);
        int pageStart = pSize * (pNum - 1);
        if (path.equalsIgnoreCase("news")) {
            count = acNewsMapper.countNotAll(type);
            pageInfo.setList(acNewsMapper.selectByParams(pageStart, pSize, type));
        }
        pageInfo.setTotal(count % pSize == 0 ? count / pSize : count / pSize + 1);
        System.out.println(pageInfo);
        return pageInfo;
    }

    @Override
    public AcNews showNews(String nid) {
        if (isNumber(nid)) {
            return acNewsMapper.selectById(Integer.parseInt(nid));
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public int addTalk(Talk talk) {
        talk.setTime(new Date());
        return talkMapper.insertTalk(talk);
    }

    @Override
    public List<Talk> showAllTalk() {
        return talkMapper.selectAll(1,"time","DESC");
    }

    @Override
    public Talk showTalk(String tid) {
        if (tid != null && isNumber(tid)) {
            return talkMapper.selectOne(Integer.parseInt(tid));
        }
        return null;
    }

    @Transactional
    @Override
    public boolean addComment(Comment comment) {
        comment.setTime(new Date());
        int result = commentMapper.insertOne(comment);
        return result == 1;
    }

    @Override
    public List<Comment> showComments(String tid) {
        if (tid != null && isNumber(tid)) {
            return commentMapper.selectAll(Integer.parseInt(tid));
        }
        return null;
    }

    @Override
    public boolean delComment(String id) {
        if (!isNumber(id)) {
            return false;
        }
        int flag = commentMapper.deleteOne(Integer.parseInt(id));
        return flag > 0;
    }

    @Transactional
    @Override
    public String removeTalk(String tid) throws IOException {
        if (!isNumber(tid)) {
            return "数据传输失败!";
        }
        Talk talk = talkMapper.selectOne(Integer.parseInt(tid));
        int result = talkMapper.deleteById(Integer.parseInt(tid));
        if (result > 0) {
            commentMapper.deleteByTid(Integer.parseInt(tid));
            String pic = talk.getPictures();
            if (!pic.equals("")) {
                pic = pic.substring(0, pic.indexOf('/', pic.indexOf('/') + 1));
                String path = UPLOAD_TOPIC + "/" + talk.getUser().getUid() + pic;
                System.out.println("删除" + path);
                File dir = new File(path);
                if (dir.exists()) {
                    FileUtils.forceDelete(dir);
                }
            }
        } else {
            return "删除失败!讨论不存在或者已经被删除!";
        }
        return "success";
    }

    @Transactional
    @Override
    public String sendComplaint(Complaint complaint) {
        List<Complaint> list = complaintMapper.selByUidTid(complaint.getFromUid(),
                complaint.getToId(), complaint.getToType());
        if (list.size() > 0) {
            return "您已举报过!";
        }
        complaint.setTime(new Date());
        int flag = complaintMapper.insertOne(complaint);
        if (flag <= 0) {
            return "发送失败!";
        }
        return "success";
    }
}
