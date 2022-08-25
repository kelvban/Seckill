package cn.hfbin.seckill.mq;

import cn.hfbin.seckill.entity.User;

import java.util.Date;

public class TestMessage {
    private Date time;
    private int num;
    private String uuid;
    private Integer size;

    public TestMessage(Date time, int num, String uuid,Integer size) {
        this.time = time;
        this.num = num;
        this.uuid = uuid;
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
