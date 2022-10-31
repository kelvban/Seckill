package cn.hfbin.seckill.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {
	
	public static final String MIAOSHA_QUEUE = "seckill.queue";
	public static final String QUEUE1 = "queue1";
	public static final String QUEUE_MANUAL = "manual";
	public static final String QUEUE = "queue";
	public static final String QUEUE_BasicQos="basicQos";

	public static final String FANOUT_EX="fanout.exchange";
	public static final String FANOUT_QUEUE="fanout.queue";
	public static final String FANOUT_QUEUE1="fanout.queue1";
	public static final String FANOUT_QUEUE2="fanout.queue2";

	public static final String DIRECT_EX="direct.exchange";
	public static final String DIRECT_QUEUE="direct.queue";
	public static final String DIRECT_QUEUE1="direct.queue1";

	public static final String TOPIC_EX="topic.exchange";
	public static final String TOPIC_QUEUE="topic.queue";
	public static final String TOPIC_QUEUE1="topic.queue1";

	public static final String HEADERS_EX="headers.exchange";
	public static final String HEADER_QUEUE="headers.whereAll";
	public static final String HEADER_QUEUE1="headers.whereAny";

	public static final String NORMAL_EX="normal.exchange";
	public static final String DEAD_EX="dead.exchange";
	public static final String NORMAL_QUEUE="normal.queue";
	public static final String DEAD_QUEUE="dead.queue";

	public static final String DELAY_DEAD_EX="delay.dead.exchange";
	public static final String DELAY_EX="delay.direct.exchange";
	public static final String DELAY_DEAD_QUEUE="delay.dead.queue";
	public static final String DELAY_QUEUE="delay.queue.qa";
	public static final String DELAY_QUEUE1="delay.queue.qb";

	public static final String DELAY_PLUGIN_EXCHANGE="delay.plugin.exchange";
	public static final String DELAY_PLUGIN_QUEUE="delay.plugin.queue";

	/*public static final String TOPIC_QUEUE1 = "topic.queue1";
	public static final String TOPIC_QUEUE2 = "topic.queue2";
	public static final String HEADER_QUEUE = "header.queue";
	public static final String TOPIC_EXCHANGE = "topicExchage";
	public static final String FANOUT_EXCHANGE = "fanoutxchage";
	public static final String HEADERS_EXCHANGE = "headersExchage";*/


	@Bean
	public MessageConverter getMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	@Bean
	public Queue queue() {
		return new Queue(MIAOSHA_QUEUE, true);
	}

	@Bean
	public Queue queue1() {
		return new Queue(QUEUE1, true);
	}

	@Bean
	public Queue manual() {
		return new Queue(QUEUE_MANUAL, true);
	}

	@Bean
	public Queue basicQos() {
		return new Queue(QUEUE_BasicQos, true);
	}

	@Bean
	public Queue fanoutQueue(){
		return new Queue(FANOUT_QUEUE,true);
	}

	@Bean
	public Queue fanoutQueue1(){
		return new Queue(FANOUT_QUEUE1,true);
	}

	@Bean
	public Queue fanoutQueue2(){
		return new Queue(FANOUT_QUEUE2,true);
	}

	@Bean
	public Queue directQueue(){
		return new Queue(DIRECT_QUEUE,true);
	}

	@Bean
	public Queue directQueue1(){
		return new Queue(DIRECT_QUEUE1,true);
	}

	@Bean
	public Queue topicQueue(){
		return new Queue(TOPIC_QUEUE,true);
	}

	@Bean
	public Queue topicQueue1(){
		return new Queue(TOPIC_QUEUE1,true);
	}

	@Bean
	public Queue headersQueue(){
		return new Queue(HEADER_QUEUE,true);
	}

	@Bean
	public Queue headersQueue1(){
		return new Queue(HEADER_QUEUE1,true);
	}


	// 获取RabbitMQ服务器连接
	public static Connection getConnection() {
		Connection connection = null;
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("mine.com");
			factory.setPort(5672);
			factory.setUsername("seckill");
			factory.setPassword("1234");
			connection = factory.newConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static Channel getChannel(){
		Connection connection=getConnection();
		Channel channel=null;
		try{
			channel=connection.createChannel();
		}catch (Exception e){
			e.printStackTrace();
		}
		return channel;
	}

	public static Channel setBasicQos(Channel channel){
		try {
			channel.basicQos(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return channel;
	}

	/**
	 * 扇出交换机
	 */
	@Bean
	public FanoutExchange fanoutExchange(){
		return new FanoutExchange(FANOUT_EX);
	}

	@Bean
	public Binding FanoutBinding(){
		return BindingBuilder.bind(fanoutQueue()).to(fanoutExchange());
	}

	@Bean
	public Binding FanoutBinding1(){
		return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
	}

	@Bean
	public Binding FanoutBinding2(){
		return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
	}
	/**
	 * 直接模式
	 */
	@Bean
	public DirectExchange directExchange(){
		return new DirectExchange(DIRECT_EX);
	}

	@Bean
	public Binding directBinding () {
		return BindingBuilder.bind(directQueue()).to(directExchange()).with("even");
	}

	@Bean
	public Binding directBinding1 () {
		return BindingBuilder.bind(directQueue1()).to(directExchange()).with("odd");
	}

	/**
	 * 主题模式
	 */
	@Bean
	public TopicExchange topicExchange(){
		return new TopicExchange(TOPIC_EX);
	}

	@Bean
	public Binding topicBinding () {
		return BindingBuilder.bind(topicQueue()).to(topicExchange()).with("topic.*.key");
	}

	@Bean
	public Binding topicBinding1 () {
		return BindingBuilder.bind(directQueue1()).to(directExchange()).with("topic.test");
	}


	/**
	 * 标题模式
	 */
	@Bean
	public HeadersExchange headersExchange(){
		return new HeadersExchange(HEADERS_EX);
	}

	@Bean
	public Binding headersBinding(){
		HashMap<String,Object> header=new HashMap<>();
		header.put("one","A");
		header.put("two","B");
		return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(header).match();
	}

	@Bean
	public Binding headersBinding1(){
		HashMap<String,Object> header=new HashMap<>();
		header.put("one","A");
		header.put("two","B");
		return BindingBuilder.bind(headersQueue1()).to(headersExchange()).whereAny(header).match();
	}

	/**
	 * 死信队列
	 */

	//死信队列处理

	@Bean
	public Queue deadQueue(){
		return new Queue(DEAD_QUEUE,true);
	}

	@Bean
	public DirectExchange deadExchage(){
		return new DirectExchange(DEAD_EX);
	}

	@Bean
	public Binding deadBinding(){
		return BindingBuilder.bind(deadQueue()).to(deadExchage()).with("dead.msg");
	}

	//业务队列处理
	@Bean
	public Queue normalQueue(){
		Map<String,Object> arguments = new HashMap<>(2);
		// 绑定该队列到死信交换机
		arguments.put("x-dead-letter-exchange",DEAD_EX);
		arguments.put("x-dead-letter-routing-key","dead.msg");
//		arguments.put("x-max-length",1000); //设置长度，模拟超过队列长度情况
		return new Queue(NORMAL_QUEUE,true,false,false,arguments);

	}
//	@Bean
//	public Queue normalQueue(){
//		return QueueBuilder
//				.durable(NORMAL_QUEUE)
//				.deadLetterExchange(DEAD_EX) // 这里声明当前队列绑定的死信交换机
//				.deadLetterRoutingKey("dead.msg")  // 这里声明当前队列的死信路由key
//				.build();
//	}

	@Bean
	public DirectExchange normalExchage(){
		return new DirectExchange(NORMAL_EX);
	}
	@Bean
	public Binding normalBinding(){
		return BindingBuilder.bind(normalQueue()).to(normalExchage()).with("normal.msg");
	}

	/**
	 * 延迟队列
	 * @return
	 */

	//绑定死信队列
	@Bean
	public Queue delayDeadQueue(){
		return new Queue(DELAY_DEAD_QUEUE,true);
	}

	@Bean
	public DirectExchange delayDeadExchange(){
		return new DirectExchange(DELAY_DEAD_EX);
	}

	@Bean
	public Binding delayDeadBinding(){
		return BindingBuilder.bind(delayDeadQueue()).to(delayDeadExchange()).with("delay.dead.msg");
	}
	//设置过期时间，模拟延迟
	@Bean
	public Queue delayQueue(){
		Map<String,Object> arguments = new HashMap<>(2);
		// 绑定该队列到死信交换机
		arguments.put("x-dead-letter-exchange",DELAY_DEAD_EX);
		arguments.put("x-dead-letter-routing-key","delay.dead.msg");
//		arguments.put("x-max-length",1000); //设置长度，模拟超过队列长度情况
		return new Queue(DELAY_QUEUE,true,false,false,arguments);
	}
	@Bean
	public Queue delayQueue1(){
		Map<String,Object> arguments = new HashMap<>(2);
		// 绑定该队列到死信交换机
		arguments.put("x-dead-letter-exchange",DELAY_DEAD_EX);
		arguments.put("x-dead-letter-routing-key","delay.dead.msg");
//		arguments.put("x-max-length",1000); //设置长度，模拟超过队列长度情况
		return new Queue(DELAY_QUEUE1,true,false,false,arguments);
	}
	@Bean
	public DirectExchange delayExchage(){
		return new DirectExchange(DELAY_EX);
	}

	@Bean
	public Binding delayBinding(){
		return BindingBuilder.bind(delayQueue()).to(delayExchage()).with("delay.qa.msg");
	}

	@Bean
	public Binding delayBinding1(){
		return BindingBuilder.bind(delayQueue1()).to(delayExchage()).with("delay.qb.msg");
	}


	/**
	 * 插件实现延迟队列
	 */
	@Bean
	public Queue delayPluginQueue(){
		return new Queue(DELAY_PLUGIN_QUEUE,true);
	}

	@Bean
	public CustomExchange delayPluginExchange(){
		Map<String,Object> arguments=new HashMap<>();
		//延时交换机一定要设置x-delayed-type属性
		arguments.put("x-delayed-type", "direct");
		//第二个参数type需要设置成x-delayed-message
		return new CustomExchange(DELAY_PLUGIN_EXCHANGE,"x-delayed-message",true,false,arguments);
	}

	@Bean
	public Binding delayPluginBinding(){
		return BindingBuilder.bind(delayPluginQueue()).to(delayPluginExchange()).with("delay.plugin.msg").noargs();
	}



	/**
	 * Direct模式 交换机Exchange
	 * */
	/*@Bean
	public Queue queue() {
		return new Queue(QUEUE, true);
	}
	@Bean
	public DirectExchange topicDirect(){
		return new DirectExchange(TOPIC_EXCHANGE);
	}
	
	*//**
	 * Topic模式 交换机Exchange
	 * *//*
	@Bean
	public Queue topicQueue1() {
		return new Queue(TOPIC_QUEUE1, true);
	}
	@Bean
	public Queue topicQueue2() {
		return new Queue(TOPIC_QUEUE2, true);
	}
	@Bean
	public TopicExchange topicExchage(){
		return new TopicExchange(TOPIC_EXCHANGE);
	}
	@Bean
	public Binding topicBinding1() {
		return BindingBuilder.bind(topicQueue1()).to(topicExchage()).with("topic.key1");
	}
	@Bean
	public Binding topicBinding2() {
		return BindingBuilder.bind(topicQueue2()).to(topicExchage()).with("topic.#");
	}
	*//**
	 * Fanout模式 交换机Exchange
	 * *//*
	@Bean
	public FanoutExchange fanoutExchage(){
		return new FanoutExchange(FANOUT_EXCHANGE);
	}
	@Bean
	public Binding FanoutBinding1() {
		return BindingBuilder.bind(topicQueue1()).to(fanoutExchage());
	}
	@Bean
	public Binding FanoutBinding2() {
		return BindingBuilder.bind(topicQueue2()).to(fanoutExchage());
	}
	*//**
	 * Header模式 交换机Exchange
	 * *//*
	@Bean
	public HeadersExchange headersExchage(){
		return new HeadersExchange(HEADERS_EXCHANGE);
	}
	@Bean
	public Queue headerQueue1() {
		return new Queue(HEADER_QUEUE, true);
	}
	@Bean
	public Binding headerBinding() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("header1", "value1");
		map.put("header2", "value2");
		return BindingBuilder.bind(headerQueue1()).to(headersExchage()).whereAll(map).match();
	}
	*/
	
}
