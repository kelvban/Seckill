package cn.hfbin.seckill.controller;

import cn.hfbin.seckill.annotations.AccessLimit;
import cn.hfbin.seckill.bo.GoodsBo;
import cn.hfbin.seckill.common.Const;
import cn.hfbin.seckill.entity.OrderInfo;
import cn.hfbin.seckill.entity.SeckillOrder;
import cn.hfbin.seckill.entity.User;
import cn.hfbin.seckill.mq.MQReceiver;
import cn.hfbin.seckill.mq.MQSender;
import cn.hfbin.seckill.mq.SeckillMessage;
import cn.hfbin.seckill.mq.TestMessage;
import cn.hfbin.seckill.redis.GoodsKey;
import cn.hfbin.seckill.redis.RedisService;
import cn.hfbin.seckill.redis.UserKey;
import cn.hfbin.seckill.result.CodeMsg;
import cn.hfbin.seckill.result.Result;
import cn.hfbin.seckill.service.SeckillGoodsService;
import cn.hfbin.seckill.service.SeckillOrderService;
import cn.hfbin.seckill.service.TestMessageService;
import cn.hfbin.seckill.util.CookieUtil;
import cn.hfbin.seckill.vo.OrderDetailVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * Created by: HuangFuBin
 * Date: 2018/7/15
 * Time: 23:55
 * Such description:
 */
@Controller
@RequestMapping("seckill")
public class SeckillController implements InitializingBean {


    @Autowired
    RedisService redisService;

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    SeckillOrderService seckillOrderService;

    @Autowired
    MQSender mqSender;

    @Resource
    TestMessageService testMessageService;

    @Resource
    private ExecutorService executorService;

    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 系统初始化
     */
    public void afterPropertiesSet() throws Exception {
        List<GoodsBo> goodsList = seckillGoodsService.getSeckillGoodsList();
        if (goodsList == null) {
            return;
        }
        for (GoodsBo goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, "" + goods.getId(), goods.getStockCount(), Const.RedisCacheExtime.GOODS_LIST);
            localOverMap.put(goods.getId(), false);
        }
    }

    @RequestMapping("/test")
    @ResponseBody
    public Result<String> test(@RequestParam Integer size,
                       @RequestParam Integer range,
                               @RequestParam String batch){
        Random random=new Random();
        List<TestMessage> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            TestMessage testMessage=new TestMessage(new Date(),random.nextInt(range), UUID.randomUUID().toString(),size,(byte) 0,batch);
            list.add(testMessage);
        }
        testMessageService.batchInsert(list);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Long startTime=System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)){
                    for (TestMessage testMessage:list){
                        mqSender.sendTestMessage(testMessage);
                    }
                }
                Long endTime=System.currentTimeMillis();
                System.out.println("推送总用时："+(endTime-startTime));
            }
        });

        return Result.success("执行完成");
    }

    @RequestMapping("/test-manual")
    @ResponseBody
    public Result<String> testManual(@RequestParam Integer size,
                               @RequestParam Integer range,
                               @RequestParam String batch){
        Random random=new Random();
        List<TestMessage> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            TestMessage testMessage=new TestMessage(new Date(),random.nextInt(range), UUID.randomUUID().toString(),size,(byte) 0,batch);
            list.add(testMessage);
        }
        testMessageService.batchInsert(list);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Long startTime=System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)){
                    for (TestMessage testMessage:list){
                        mqSender.sendTestManualMessage(testMessage);
                    }
                }
                Long endTime=System.currentTimeMillis();
                System.out.println("推送总用时："+(endTime-startTime));
            }
        });

        return Result.success("执行完成");
    }

    @RequestMapping("/test-basicQos")
    @ResponseBody
    public Result<String> testBasicQos(@RequestParam Integer size,
                                     @RequestParam Integer range,
                                     @RequestParam String batch){
        Random random=new Random();
        List<TestMessage> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            TestMessage testMessage=new TestMessage(new Date(),random.nextInt(range), UUID.randomUUID().toString(),size,(byte) 0,batch);
            list.add(testMessage);
        }
        testMessageService.batchInsert(list);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Long startTime=System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)){
                    for (TestMessage testMessage:list){
                        mqSender.sendTestBasicQosMessage(testMessage);
                    }
                }
                Long endTime=System.currentTimeMillis();
                System.out.println("推送总用时："+(endTime-startTime));
            }
        });

        return Result.success("执行完成");
    }


    @RequestMapping("/test-fanout")
    @ResponseBody
    public Result<String> testFanout(@RequestParam Integer size,
                                       @RequestParam Integer range,
                                       @RequestParam String batch){
        Random random=new Random();
        List<TestMessage> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            TestMessage testMessage=new TestMessage(new Date(),random.nextInt(range), UUID.randomUUID().toString(),size,(byte) 0,batch);
            list.add(testMessage);
        }
        testMessageService.batchInsert(list);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Long startTime=System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)){
                    for (TestMessage testMessage:list){
                        mqSender.sendFanoutMessage(testMessage);
                    }
                }
                Long endTime=System.currentTimeMillis();
                System.out.println("推送总用时："+(endTime-startTime));
            }
        });

        return Result.success("执行完成");
    }

    @RequestMapping("/test-direct")
    @ResponseBody
    public Result<String> testDirect(@RequestParam Integer size,
                                     @RequestParam Integer range,
                                     @RequestParam String batch){
        Random random=new Random();
        List<TestMessage> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            TestMessage testMessage=new TestMessage(new Date(),random.nextInt(range), UUID.randomUUID().toString(),size,(byte) 0,batch);
            list.add(testMessage);
        }
        testMessageService.batchInsert(list);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Long startTime=System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)){
//                    for (TestMessage testMessage:list){
//                        mqSender.sendFanoutMessage(testMessage);
//                    }
                    for(int i=0;i<list.size();i++){
//                        if(i%2==0){
//                            mqSender.sendDirectMessage(list.get(i),"even");
//                        }else {
//                            mqSender.sendDirectMessage(list.get(i),"odd");
//                        }
                        mqSender.sendDirectMessage(list.get(i),"even");
                    }
                }
                Long endTime=System.currentTimeMillis();
                System.out.println("推送总用时："+(endTime-startTime));
            }
        });

        return Result.success("执行完成");
    }


    @RequestMapping("/test-topic")
    @ResponseBody
    public Result<String> testTopic(@RequestParam Integer size,
                                     @RequestParam Integer range,
                                     @RequestParam String batch){
        Random random=new Random();
        List<TestMessage> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            TestMessage testMessage=new TestMessage(new Date(),random.nextInt(range), UUID.randomUUID().toString(),size,(byte) 0,batch);
            list.add(testMessage);
        }
        testMessageService.batchInsert(list);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Long startTime=System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)){
                    for (TestMessage testMessage:list){
                        mqSender.sendTopicMessage(testMessage,"topic.test.key");
                    }
                }
                Long endTime=System.currentTimeMillis();
                System.out.println("推送总用时："+(endTime-startTime));
            }
        });

        return Result.success("执行完成");
    }


    @RequestMapping("/test-header")
    @ResponseBody
    public Result<String> testHeader(@RequestParam Integer size,
                                    @RequestParam Integer range,
                                    @RequestParam String batch){
        Random random=new Random();
        List<TestMessage> list=new ArrayList<>();
        for(int i=0;i<size;i++){
            TestMessage testMessage=new TestMessage(new Date(),random.nextInt(range), UUID.randomUUID().toString(),size,(byte) 0,batch);
            list.add(testMessage);
        }
        testMessageService.batchInsert(list);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Long startTime=System.currentTimeMillis();
                if(CollectionUtils.isNotEmpty(list)){
                    for (TestMessage testMessage:list){
                        mqSender.sendHeaderMessage(testMessage);
                    }
                }
                Long endTime=System.currentTimeMillis();
                System.out.println("推送总用时："+(endTime-startTime));
            }
        });

        return Result.success("执行完成");
    }


    @RequestMapping("/seckill2")
    public String list2(Model model,
                        @RequestParam("goodsId") long goodsId, HttpServletRequest request) {

        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        //判断库存
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillOrderService.insert(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }

    @RequestMapping(value = "/{path}/seckill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model,
                                @RequestParam("goodsId") long goodsId,
                                @PathVariable("path") String path,
                                HttpServletRequest request) {

        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        //验证path
        boolean check = seckillOrderService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }/**/
        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, "" + goodsId);//10
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        mqSender.sendSeckillMessage(mm);
        return Result.success(0);//排队中
        /*//判断库存
        GoodsBo goods = seckillGoodsService.getseckillGoodsBoByGoodsId(goodsId);
        if(goods == null) {
            return Result.error(CodeMsg.NO_GOODS);
        }
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = seckillOrderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillOrderService.insert(user, goods);
        return Result.success(orderInfo);*/
    }

    /**
     * 客户端轮询查询是否下单成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(@RequestParam("goodsId") long goodsId, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        long result = seckillOrderService.getSeckillResult((long) user.getId(), goodsId);
        return Result.success(result);
    }
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, User user,
                                         @RequestParam("goodsId") long goodsId) {
        String loginToken = CookieUtil.readLoginToken(request);
        user = redisService.get(UserKey.getByName, loginToken, User.class);
        if (user == null) {
            return Result.error(CodeMsg.USER_NO_LOGIN);
        }
        String path = seckillOrderService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }
}
