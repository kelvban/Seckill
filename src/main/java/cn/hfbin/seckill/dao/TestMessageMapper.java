package cn.hfbin.seckill.dao;

import cn.hfbin.seckill.mq.TestMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TestMessageMapper {
    /**
     * 批量添加测试信息
     * @return
     */
    int batchInsert(@Param("list") List<TestMessage> list);

    int updateFlagByUUID(@Param("uuid") String uuid);
}
