package cn.hfbin.seckill.service.ipml;

import cn.hfbin.seckill.dao.TestMessageMapper;
import cn.hfbin.seckill.mq.TestMessage;
import cn.hfbin.seckill.service.TestMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class TestMessageServiceImpl implements TestMessageService {
    @Resource
    TestMessageMapper testMessageMapper;

    @Override
    public int batchInsert(List<TestMessage> list) {
        return testMessageMapper.batchInsert(list);
    }

    @Override
    public int updateFlagByUUID(String uuid) {
        return testMessageMapper.updateFlagByUUID(uuid);
    }
}
