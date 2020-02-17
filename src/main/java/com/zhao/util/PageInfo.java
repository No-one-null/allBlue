package com.zhao.util;

import java.util.List;

public class PageInfo {
    //每页显示个数
    private int pageSize;
    //总个数
    private long count;
    //当前第几页
    private int pageNumber;
    //总页数
    private long total;
    //当前页显示的数据
    private List<?> list;

    public PageInfo() {
    }

    @Override
    public String toString() {
        return "PageInfo{" +
                "pageSize=" + pageSize +
                ", count=" + count +
                ", pageNumber=" + pageNumber +
                ", total=" + total +
                ", list=" + list +
                '}';
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }
}
