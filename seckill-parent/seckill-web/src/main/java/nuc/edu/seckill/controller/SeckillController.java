package nuc.edu.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.common.base.BaseRequest;
import nuc.edu.seckill.common.base.BaseResponse;
import nuc.edu.seckill.common.exception.ErrorMessage;
import nuc.edu.seckill.common.model.CommonWebUser;
import nuc.edu.seckill.model.http.SeckillReq;
import nuc.edu.seckill.model.http.SeckillReqV2;
import nuc.edu.seckill.model.http.mq.SendMsgReq;
import nuc.edu.seckill.security.WebUserUtil;
import nuc.edu.seckill.service.SeckillService;
import nuc.edu.seckill.util.MqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/seckill")
@Slf4j
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private MqSender mqService;


    /**
     * 秒杀下单（简易下单逻辑）
     */
    @RequestMapping(value = "/simple/order")
    public BaseResponse sOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        CommonWebUser user = WebUserUtil.getLoginUser();
        if (Objects.isNull(user)) {
            return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
        }
        SeckillReq req = request.getData();
        req.setUserId(user.getId());
        return seckillService.sOrder(req);
    }

    @RequestMapping(value = "/synchronized/order")
    public synchronized BaseResponse synchronizedOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        CommonWebUser user = WebUserUtil.getLoginUser();
        if (Objects.isNull(user)) {
            return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
        }
        SeckillReq req = request.getData();
        req.setUserId(user.getId());
        return seckillService.sOrder(req);
    }
    /**
     * 秒杀下单（避免超卖问题——悲观锁）
     */
    @RequestMapping(value = "/pessimistic/order")
    public BaseResponse pOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        CommonWebUser user = WebUserUtil.getLoginUser();
        if (Objects.isNull(user)) {
            return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
        }
        SeckillReq req = request.getData();
        req.setUserId(user.getId());
        return seckillService.pOrder(req);
    }

    /**
     * 秒杀下单（避免超卖问题——乐观锁）
     */
    @RequestMapping(value = "/optimistic/order")
    public BaseResponse oOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReq req = request.getData();
            req.setUserId(user.getId());
            return seckillService.oOrder(req);
        } catch (Exception e) {
            log.error("===[秒杀异常！]===", e);
        }
        return BaseResponse.error(ErrorMessage.SYS_ERROR);
    }
    /**
     * 秒杀下单（避免超卖问题——redission）
     */
    @RequestMapping(value = "/redission/order")
    public BaseResponse rOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        CommonWebUser user = WebUserUtil.getLoginUser();
        if (Objects.isNull(user)) {
            return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
        }
        SeckillReq req = request.getData();
        req.setUserId(user.getId());
        return seckillService.redissonOrder(req);
    }
    /**
     * 秒杀下单（避免超卖问题——redis缓存库存，保证原子扣减库存）
     */
    @RequestMapping(value = "/cache/order")
    public BaseResponse cOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReq req = request.getData();
            req.setUserId(user.getId());
            return seckillService.cOrder(req);
        } catch (Exception e) {
            log.error("===秒杀异常！===", e);
        }
        return BaseResponse.error(ErrorMessage.SYS_ERROR);
    }

    /**
     * 秒杀下单优化（应用限流）
     */
    @RequestMapping(value = "/v1/order")
    public BaseResponse orderV1(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReq req = request.getData();
            req.setUserId(user.getId());
            return seckillService.orderV1(req);
        } catch (Exception e) {
            log.error("===秒杀发生异常！===", e);
        }
        return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
    }

    /**
     * nginx+lua秒杀设计 + 下单扣库存接口
     */
    @RequestMapping(value = "/createOrder")
    public BaseResponse createOrder(@Valid @RequestBody BaseRequest<SeckillReq> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReq req = request.getData();
            req.setUserId(user.getId());
            return seckillService.createOrder(req);
        } catch (Exception e) {
            log.error("===秒杀发生异常！===", e);
        }
        return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
    }

    /**
     * 秒杀下单优化（单用户频次限制）
     */
    @RequestMapping(value = "/v4/order")
    public BaseResponse orderV4(@Valid @RequestBody BaseRequest<SeckillReqV2> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReqV2 req = request.getData();
            req.setUserId(user.getId());
            return seckillService.orderV4(req);
        } catch (Exception e) {
            log.error("===秒杀发生异常！===", e);
        }
        return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
    }

    /**
     * 秒杀下单优化（缓存数据库一致性——先删除缓存，再更新数据库）
     */
    @RequestMapping(value = "/v5/order")
    public BaseResponse orderV5(@Valid @RequestBody BaseRequest<SeckillReqV2> request) {
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            SeckillReqV2 req = request.getData();
            req.setUserId(user.getId());
            return seckillService.orderV5(req);
        } catch (Exception e) {
            log.error("===秒杀发生异常！===", e);
        }
        return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
    }

    private static final int DELAY_MILLSECONDS = 1000;
    // 延时双删线程池
    private static ExecutorService cachedThreadPool = new ThreadPoolExecutor(0,
            Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    /**
     * 延时双删
     */
    @RequestMapping(value = "/v6/order")
    public BaseResponse orderV6(@Valid @RequestBody BaseRequest<SeckillReqV2> request) {
        SeckillReqV2 req = request.getData();
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            req.setUserId(user.getId());
            return seckillService.orderV5(req);
        } catch (Exception e) {
            log.error("===秒杀发生异常！===", e);
            return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
        } finally {
            // 延时指定时间后再次删除缓存
            cachedThreadPool.execute(new delCacheByThread(req.getProductId()));
        }
    }
    /**
     * 缓存再删除线程
     */
    private class delCacheByThread implements Runnable {
        private Long sid;
        public delCacheByThread(Long sid) {
            this.sid = sid;
        }
        public void run() {
            try {
                log.info("异步执行缓存再删除，商品id：[{}]， 首先休眠：[{}] 毫秒", sid, DELAY_MILLSECONDS);
                Thread.sleep(DELAY_MILLSECONDS);
                seckillService.delStockByCache(sid);
                log.info("再次删除商品id：[{}] 缓存", sid);
            } catch (Exception e) {
                log.error("delCacheByThread执行出错", e);
            }
        }
    }

    /**
     * 删除缓存重试机制
     */
    @RequestMapping(value = "/v7/order")
    public BaseResponse orderV7(@Valid @RequestBody BaseRequest<SeckillReqV2> request) {
        SeckillReqV2 req = request.getData();
        try {
            CommonWebUser user = WebUserUtil.getLoginUser();
            if (Objects.isNull(user)) {
                return BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }
            req.setUserId(user.getId());
            return seckillService.orderV5(req);
        } catch (Exception e) {
            log.error("===秒杀发生异常！===", e);
            return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
        } finally {
            // 消息队列处理 “缓存删除重试” 逻辑
            mqService.sendDelCacheMsg(new SendMsgReq.DelCacheReq().setProductId(req.getProductId()));
        }
    }






}
