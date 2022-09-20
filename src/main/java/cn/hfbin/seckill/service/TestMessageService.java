package cn.hfbin.seckill.service;

import cn.hfbin.seckill.mq.TestMessage;

import java.util.List;

public interface TestMessageService {
    int batchInsert(List<TestMessage> list);

    int updateFlagByUUID(String uuid);
}
