package cn.hfbin.seckill.mq;

import cn.hfbin.seckill.bo.GoodsBo;
import cn.hfbin.seckill.entity.SeckillOrder;
import cn.hfbin.seckill.entity.User;
import cn.hfbin.seckill.redis.RedisService;
import cn.hfbin.seckill.service.OrderService;
import cn.hfbin.seckill.service.SeckillGoodsService;
import cn.hfbin.seckill.service.SeckillOrderService;
import cn.hfbin.seckill.service.TestMessageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MQReceiver {

		private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

		private static Map<String,Object> map=new HashMap();

		private static ReentrantLock lock=new ReentrantLock();

		private static Set<String> set=new HashSet<>();
		private static Set<String> set1=new HashSet<>();

		
		@Autowired
		RedisService redisService;
		
		@Autowired
		SeckillGoodsService goodsService;
		
		@Autowired
		OrderService orderService;
		
		@Autowired
		SeckillOrderService seckillOrderService;

		@Resource
		private TestMessageService testMessageService;
		
		@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
		public void receive(String message) {
			log.info("receive message:"+message);
			SeckillMessage mm  = RedisService.stringToBean(message, SeckillMessage.class);
			User user = mm.getUser();
			long goodsId = mm.getGoodsId();
			
			GoodsBo goods = goodsService.getseckillGoodsBoByGoodsId(goodsId);
	    	int stock = goods.getStockCount();
	    	if(stock <= 0) {
	    		return;
	    	}
	    	//判断是否已经秒杀到了
	    	SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
	    	if(order != null) {
	    		return;
	    	}
	    	//减库存 下订单 写入秒杀订单
			seckillOrderService.insert(user, goods);
		}

//		@RabbitListener(queues = MQConfig.QUEUE1)
//		public void test(String message){
//			System.out.println(message);
//			log.info("receive message:"+message);
//			JSONObject jsonObject  = JSONObject.parseObject(message);
//			TestMessage testMessage=new TestMessage(jsonObject.getDate("time"),
//					jsonObject.getInteger("num"),
//					jsonObject.getString("uuid"),
//					jsonObject.getInteger("size"));
//			String uuid=testMessage.getUuid();
//			map.put(uuid,testMessage);
//			int max=0;
//			TestMessage ct=null;
//			if(map.size()==testMessage.getSize()){
//				for(String key:map.keySet()){
//					TestMessage t= (TestMessage) map.get(key);
//					Integer num=t.getNum();
//					if(max<num){
//						max=num;
//						ct=testMessage;
//					}
//				}
//				log.info("----------------------------------------------");
//				log.info("Test current max num:"+RedisService.beanToString(ct));
//				log.info("----------------------------------------------");
//				map.clear();
//			}
//			System.out.println("---------------"+map.size()+"---------------");
//		}
//
//		@RabbitListener(queues = MQConfig.QUEUE1)
//		public void test1(String message){
//			System.out.println(message);
//			log.info("receive message:"+message);
//			JSONObject jsonObject  = JSONObject.parseObject(message);
//			TestMessage testMessage=new TestMessage(jsonObject.getDate("time"),
//					jsonObject.getInteger("num"),
//					jsonObject.getString("uuid"),
//					jsonObject.getInteger("size"));
//			String uuid=testMessage.getUuid();
//
//			int max=0;
//			TestMessage ct=null;
//			if(map.size()==testMessage.getSize()){
//				for(String key:map.keySet()){
//					TestMessage t= (TestMessage) map.get(key);
//					Integer num=t.getNum();
//					if(max<num){
//						max=num;
//						ct=testMessage;
//					}
//				}
//				log.info("----------------------------------------------");
//				log.info("Test1 current max num:"+RedisService.beanToString(ct));
//				log.info("----------------------------------------------");
//				map.clear();
//			}
//			System.out.println("---------------"+map.size()+"---------------");
//		}

	@RabbitListener(queues = MQConfig.QUEUE1)
	public void test(String message){
//		System.out.println(message);
//		log.info("receive message:"+message);
		JSONObject jsonObject  = JSONObject.parseObject(message);
		TestMessage testMessage=new TestMessage(jsonObject.getDate("time"),
				jsonObject.getInteger("num"),
				jsonObject.getString("uuid"),
				jsonObject.getInteger("size"),
				jsonObject.getByte("flag"),
				jsonObject.getString("batch"));
		if(testMessage!=null&& StringUtils.isNotBlank(testMessage.getUuid())){
			testMessageService.updateFlagByUUID(testMessage.getUuid());
		}

//		synchronized (set){
//			set.add(testMessage.getUuid());
//			System.out.println("---------------set:"+set.size()+"---------------");
//		}
		//		if(lock.tryLock()){
		lock.lock();
		try {
			set.add(testMessage.getUuid());
			System.out.println("---------------set:"+set.size()+"---------------");
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
//		}
//		else {
//			set.add(testMessage.getUuid());
//			System.out.println("---------------set:"+set.size()+"---------------");
//		}
	}

	@RabbitListener(queues = MQConfig.QUEUE1)
	public void test1(String message){
//		System.out.println(message);
//		log.info("receive message:"+message);
		JSONObject jsonObject  = JSONObject.parseObject(message);
		TestMessage testMessage=new TestMessage(jsonObject.getDate("time"),
				jsonObject.getInteger("num"),
				jsonObject.getString("uuid"),
				jsonObject.getInteger("size"),
				jsonObject.getByte("flag"),
				jsonObject.getString("batch"));
		if(testMessage!=null&& StringUtils.isNotBlank(testMessage.getUuid())){
			testMessageService.updateFlagByUUID(testMessage.getUuid());
		}
//		synchronized (set){
//			set.add(testMessage.getUuid());
//			System.out.println("---------------set:"+set.size()+"---------------");
//		}
//		if(lock.tryLock()){
		lock.lock();
		try {
			set.add(testMessage.getUuid());
			System.out.println("---------------set:"+set.size()+"---------------");
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
//		}
//		else {
//			set.add(testMessage.getUuid());
//			System.out.println("---------------set:"+set.size()+"---------------");
//		}
//		set.add(testMessage.getUuid());
//		System.out.println("---------------set1:"+set.size()+"---------------");
	}

	@RabbitListener(queues = MQConfig.QUEUE1)
	public void test2(String message){
//		System.out.println(message);
//		log.info("receive message:"+message);
		JSONObject jsonObject  = JSONObject.parseObject(message);
		TestMessage testMessage=new TestMessage(jsonObject.getDate("time"),
				jsonObject.getInteger("num"),
				jsonObject.getString("uuid"),
				jsonObject.getInteger("size"),
				jsonObject.getByte("flag"),
				jsonObject.getString("batch"));
		if(testMessage!=null&& StringUtils.isNotBlank(testMessage.getUuid())){
			testMessageService.updateFlagByUUID(testMessage.getUuid());
		}
//		synchronized (set){
//			set.add(testMessage.getUuid());
//			System.out.println("---------------set:"+set.size()+"---------------");
//		}
// 		if(lock.tryLock()){
		lock.lock();
		try {
			set.add(testMessage.getUuid());
			System.out.println("---------------set:"+set.size()+"---------------");
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
//		}
//		else {
//			set.add(testMessage.getUuid());
//			System.out.println("---------------set:"+set.size()+"---------------");
//		}
//		set.add(testMessage.getUuid());
//		System.out.println("---------------set1:"+set.size()+"---------------");
	}
}
