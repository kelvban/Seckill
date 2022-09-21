package cn.hfbin.seckill.mq;

import cn.hfbin.seckill.entity.User;
import lombok.Data;

import java.util.Date;

@Data
public class TestMessage {
    private Date time;
    private int num;
    private String uuid;
    private Integer size;
    private Byte flag;
    private String batch;

    public TestMessage(Date time, int num, String uuid,Integer size,Byte flag,String batch) {
        this.time = time;
        this.num = num;
        this.uuid = uuid;
        this.size = size;
        this.flag=flag;
        this.batch=batch;
    }

}
