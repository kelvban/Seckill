package cn.hfbin.seckill.mq;

import cn.hfbin.seckill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MQSender {

	private static Logger log = LoggerFactory.getLogger(MQSender.class);
	
	@Autowired
	AmqpTemplate amqpTemplate ;
	
	public void sendSeckillMessage(SeckillMessage mm) {
		String msg = RedisService.beanToString(mm);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
	}

	public void sendTestMessage(TestMessage testMessage) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.QUEUE1, msg);
	}

	public void sendTestManualMessage(TestMessage testMessage) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.QUEUE_MANUAL, msg);
	}


	public void sendTestBasicQosMessage(TestMessage testMessage) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.QUEUE_BasicQos, msg);
	}

	public void sendFanoutMessage(TestMessage testMessage) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.FANOUT_EX,"", msg);
	}

	public void sendDirectMessage(TestMessage testMessage,String routeKey) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.DIRECT_EX,routeKey, msg);
	}


	public void sendTopicMessage(TestMessage testMessage,String routeKey) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		amqpTemplate.convertAndSend(MQConfig.TOPIC_EX,routeKey, msg);
	}

	public void sendHeaderMessage(TestMessage testMessage) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader("one", "A");
		messageProperties.setHeader("two", "B");
		Message message = new Message(msg.getBytes(), messageProperties);
		amqpTemplate.convertAndSend(MQConfig.HEADERS_EX,null, message);
	}

	public void sendDeadMessage(TestMessage testMessage) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		messageProperties.setExpiration("10000");
		Message message = new Message(msg.getBytes(), messageProperties);
		amqpTemplate.convertAndSend(MQConfig.NORMAL_EX,"normal.msg", message);
	}


	public void sendDelayABMessage(TestMessage testMessage,String routingKey,Long tll) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
		messageProperties.setExpiration(tll.toString());
		Message message = new Message(msg.getBytes(), messageProperties);
		amqpTemplate.convertAndSend(MQConfig.DELAY_EX,routingKey, message);
	}

	public void sendDelayPluginMessage(TestMessage testMessage,String routingKey,Long tll) {
		String msg = RedisService.beanToString(testMessage);
		log.info("send message:"+msg);
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
//		messageProperties.setExpiration(tll.toString());
		messageProperties.setHeader("x-delay",tll.toString()); //设置header中key为x-delay，其余不生效
		Message message = new Message(msg.getBytes(), messageProperties);
		amqpTemplate.convertAndSend(MQConfig.DELAY_PLUGIN_EXCHANGE,routingKey, message);
	}

}
