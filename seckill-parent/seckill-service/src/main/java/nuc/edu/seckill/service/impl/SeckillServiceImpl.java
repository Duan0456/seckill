package nuc.edu.seckill.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.common.base.BaseResponse;
import nuc.edu.seckill.common.exception.ErrorMessage;
import nuc.edu.seckill.dao.SeckillOrderDao;
import nuc.edu.seckill.dao.SeckillProductsDao;
import nuc.edu.seckill.dao.SeckillUserDao;
import nuc.edu.seckill.model.SeckillOrder;
import nuc.edu.seckill.model.SeckillProducts;
import nuc.edu.seckill.model.SeckillUser;
import nuc.edu.seckill.model.http.SeckillReq;
import nuc.edu.seckill.model.http.SeckillReqV2;
import nuc.edu.seckill.service.Constant;
import nuc.edu.seckill.service.SeckillService;
import nuc.edu.seckill.util.DecrCacheStockUtil;
import nuc.edu.seckill.util.DistrubuteLimit;
import nuc.edu.seckill.util.RedisUtil;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;


@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    DistrubuteLimit distrubuteLimit;

    @Autowired
    DecrCacheStockUtil decrCacheStockUtil;

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Autowired
    private SeckillProductsDao seckillProductsDao;

    @Autowired
    private SeckillUserDao seckillUserDao;

    @Autowired
    private Redisson redisson;


    @Autowired
    RedisUtil redisUtil;

    // Guava令牌桶：每秒放行5个请求
    RateLimiter rateLimiter = RateLimiter.create(5);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse sOrder(SeckillReq req) {
        log.info("===[开始调用原始下单接口~]===");
        //参数校验
        log.info("===[校验用户信息及商品信息]===");
        BaseResponse paramValidRes = validateParam(req.getProductId(), req.getUserId());
        if (paramValidRes.getCode() != 0) {
            return paramValidRes;
        }
        log.info("===[校验参数是否合法][通过]===");
        Long productId = req.getProductId();
        Long userId = req.getUserId();
        SeckillProducts product = seckillProductsDao.selectByPrimaryKey(productId);
        Date date = new Date();
        // 扣减库存
        log.info("===[开始扣减库存]===");
        product.setSaled(product.getSaled() + 1);
        seckillProductsDao.updateByPrimaryKeySelective(product);
        log.info("===[扣减库存][成功]===");
        // 创建订单
        log.info("===[开始创建订单]===");
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        seckillOrderDao.insert(order);
        log.info("===[创建订单][成功]===");
        return BaseResponse.OK;
    }


    private BaseResponse validateParam(Long productId, Long userId) {
        SeckillProducts product = seckillProductsDao.selectByPrimaryKey(productId);
        if (product == null) {
            log.error("===[产品不存在！]===");
            return BaseResponse.error(ErrorMessage.SYS_ERROR);
        }

        if (product.getStartBuyTime().getTime() > System.currentTimeMillis()) {
            log.error("===[秒杀还未开始！]===");
            return BaseResponse.error(ErrorMessage.SECKILL_NOT_START);
        }

        if (product.getSaled() >= product.getCount()) {
            log.error("===[库存不足！]===");
            return BaseResponse.error(ErrorMessage.STOCK_NOT_ENOUGH);
        }
        if (hasOrderedUserCache(productId, userId)) {
            log.error("===[用户重复下单！]===");
            return BaseResponse.error(ErrorMessage.REPEAT_ORDER_ERROR);
        }

        return BaseResponse.OK;
    }

    /**
     * 悲观锁实现方式
     *
     * @param req
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse pOrder(SeckillReq req) {
        log.info("===[开始调用秒杀接口(悲观锁)]===");
        //校验用户信息、商品信息、库存信息
        log.info("===[校验用户信息、商品信息、库存信息]===");
        BaseResponse paramValidRes = validateParamPessimistic(req.getProductId(), req.getUserId());
        if (paramValidRes.getCode() != 0) {
            return paramValidRes;
        }
        log.info("===[校验][通过]===");
        log.info("===[校验参数是否合法][通过]===");

        log.info("===[校验 用户是否重复下单]===");
        SeckillOrder param = new SeckillOrder();
        param.setProductId(req.getProductId());
        param.setUserId(req.getUserId());
        int repeatCount = seckillOrderDao.count(param);
        if(repeatCount>0) {
            log.error("用户重复下单");
            return BaseResponse.error(ErrorMessage.REPEAT_ORDER_ERROR);
        }
        log.info("是否重复下单，通过校验");

        Long userId = req.getUserId();
        Long productId = req.getProductId();
        SeckillProducts product = seckillProductsDao.selectByPrimaryKey(productId);
        // 下单逻辑
        log.info("===[开始下单逻辑]===");
        Date date = new Date();
        // 扣减库存
        product.setSaled(product.getSaled() + 1);
        seckillProductsDao.updateByPrimaryKeySelective(product);
        // 创建订单
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(product.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        seckillOrderDao.insert(order);
        return BaseResponse.OK;
    }
    // 悲观锁实现的校验逻辑
    private BaseResponse validateParamPessimistic(Long productId, Long userId) {
        //悲观锁，利用selectForUpdate方法锁定记录，并获得最新的SeckillProducts记录
        SeckillProducts product = seckillProductsDao.selectForUpdate(productId);
        if (product == null) {
            log.error("===[产品不存在！]===");
            return BaseResponse.error(ErrorMessage.SYS_ERROR);
        }
        if (product.getStartBuyTime().getTime() > System.currentTimeMillis()) {
            log.error("===[秒杀还未开始！]===");
            return BaseResponse.error(ErrorMessage.SECKILL_NOT_START);
        }
        if (product.getSaled() >= product.getCount()) {
            log.error("===[库存不足！]===");
            return BaseResponse.error(ErrorMessage.STOCK_NOT_ENOUGH);
        }
        return BaseResponse.OK;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse oOrder(SeckillReq req) throws Exception {
        log.info("===[开始调用下单接口~（乐观锁）]===");
        //参数校验
        log.info("===[校验参数是否合法]===");
        BaseResponse paramValidRes = validateParam(req.getProductId(), req.getUserId());
        if (paramValidRes.getCode() != 0) {
            return paramValidRes;
        }
        log.info("===[校验参数是否合法][通过]===");
        //下单（乐观锁）
        return createOptimisticOrder(req.getProductId(), req.getUserId());
    }


    private BaseResponse createOptimisticOrder(Long productId, Long userId) throws Exception {
        log.info("===[下单逻辑Starting]===");
        // 创建订单
        SeckillProducts products = seckillProductsDao.selectByPrimaryKey(productId);
        Date date = new Date();
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(products.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        seckillOrderDao.insert(order);
        log.info("===[创建订单成功]===");
        //扣减库存
        int res = seckillProductsDao.updateStockByOptimistic(productId);
        if (res == 0) {
            log.error("===[秒杀失败，抛出异常，执行回滚逻辑！]===");
            throw new Exception("库存不足");
//          return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
        }
        log.info("===[扣减库存成功!]===");
        try {
            addOrderedUserCache(productId, userId);
        } catch (Exception e) {
            log.error("===[记录已购用户缓存时发生异常！]===", e);
        }
        return BaseResponse.OK;
    }
    public void addOrderedUserCache(Long productId, Long userId) {
        String key = String.format(Constant.redisKey.SECKILL_ORDERED_USER, productId);
        redisUtil.sSet(key, userId);
        log.info("===[已将已购用户放入缓存！]===");
    }
    public Boolean hasOrderedUserCache(Long productId, Long userId) {
        String key = String.format(Constant.redisKey.SECKILL_ORDERED_USER, productId);
        return redisUtil.sHasKey(key, userId);
    }



    @Override
    public BaseResponse redissonOrder(SeckillReq req) {
        log.info("===[开始调用下单接口（redission）~]===");
        String lockKey = String.format(Constant.redisKey.SECKILL_DISTRIBUTED_LOCK, req.getProductId());
        RLock lock = redisson.getLock(lockKey);
        try {
            //将锁过期时间设置为30s，定时任务每隔10秒执行续锁操作
            lock.lock();
            //** 另一种写法 **
            //先拿锁，在设置超时时间，看门狗就不会自动续期，锁到达过期时间后，就释放了
            //lock.lock(30, TimeUnit.SECONDS);
            //参数校验
            log.info("===[校验用户信息及商品信息]===");
            BaseResponse paramValidRes = validateParam(req.getProductId(), req.getUserId());
            if (paramValidRes.getCode() != 0) {
                return paramValidRes;
            }
            log.info("===[校验参数是否合法][通过]===");
            Long productId = req.getProductId();
            Long userId = req.getUserId();
            SeckillProducts product = seckillProductsDao.selectByPrimaryKey(productId);
            Date date = new Date();
            // 扣减库存
            log.info("===[开始扣减库存]===");
            product.setSaled(product.getSaled() + 1);
            seckillProductsDao.updateByPrimaryKeySelective(product);
            log.info("===[扣减库存][成功]===");
            // 创建订单
            log.info("===[开始创建订单]===");
            SeckillOrder order = new SeckillOrder();
            order.setProductId(productId);
            order.setProductName(product.getName());
            order.setUserId(userId);
            order.setCreateTime(date);
            seckillOrderDao.insert(order);
            log.info("===[创建订单][成功]===");
            return BaseResponse.OK;
        } catch (Exception e) {
            log.error("===[异常！]===", e);
            return BaseResponse.error(ErrorMessage.SYS_ERROR);
        } finally {
            lock.unlock(); // 释放redis分布式锁
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse cOrder(SeckillReq req) throws Exception {
        log.info("===[开始调用下单接口~（避免超卖——Redis）]===");
        long res = 0;
        try {
            //校验用户信息、商品信息、库存信息
            log.info("===[校验用户信息、商品信息、库存信息]===");
            BaseResponse paramValidRes = validateParam(req.getProductId(), req.getUserId());
            if (paramValidRes.getCode() != 0) {
                return paramValidRes;
            }
            log.info("===[校验][通过]===");
            Long productId = req.getProductId();
            Long userId = req.getUserId();
            //redis + lua
            res = decrCacheStockUtil.decrStock(req.getProductId());
            if (res == 2) {
                // 扣减完的库存只要大于等于0，就说明扣减成功
                // 开始数据库扣减库存逻辑
                seckillProductsDao.decrStock(productId);
                // 创建订单
                SeckillProducts product = seckillProductsDao.selectByPrimaryKey(productId);
                Date date = new Date();
                SeckillOrder order = new SeckillOrder();
                order.setProductId(productId);
                order.setProductName(product.getName());
                order.setUserId(userId);
                order.setCreateTime(date);
                seckillOrderDao.insert(order);
                return BaseResponse.OK;
            } else {
                log.error("===[缓存扣减库存不足！]===");
                return BaseResponse.error(ErrorMessage.STOCK_NOT_ENOUGH);
            }
        } catch (Exception e) {
            log.error("===[异常！]===", e);
            if (res == 2) {
                decrCacheStockUtil.addStock(req.getProductId());
            }
            throw new Exception("异常！");
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse orderV1(SeckillReq req) throws Exception {
        log.info("===[开始调用下单接口（应用限流）]===");
        /**
         *  增加应用限流
         *  阻塞式 & 非阻塞式
         */
        log.info("===[开始经过限流程序]===");

        //  阻塞式获取令牌
//        log.info("===[令牌桶限流:等待时间{}]===", rateLimiter.acquire());

        //  非阻塞式获取令牌
//        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
//            log.error("你被限流了！直接返回失败！");
//            return BaseResponse.error(ErrorMessage.SECKILL_RATE_LIMIT_ERROR);
//        }

        //分布式限流
        try {
            if (!distrubuteLimit.exec()) {
                log.info("你被分布式锁限流了！直接返回失败！");
                return BaseResponse.error(ErrorMessage.SECKILL_RATE_LIMIT_ERROR);
            }
        } catch (IOException e) {
            log.error("===[分布式限流程序发生异常！]===", e);
            return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
        }


        log.info("===[限流程序][通过]===");

        //校验用户信息、商品信息、库存信息
        log.info("===[校验用户信息、商品信息、库存信息]===");
        BaseResponse paramValidRes = validateParam(req.getProductId(), req.getUserId());
        if (paramValidRes.getCode() != 0) {
            return paramValidRes;
        }
        log.info("===[校验][通过]===");
        //下单（乐观锁）
        return createOptimisticOrder(req.getProductId(), req.getUserId());
    }

    @Override
    public BaseResponse createOrder(SeckillReq req) {
        Long productId = req.getProductId();
        Long userId = req.getUserId();
        // 创建订单
        SeckillProducts products = seckillProductsDao.selectByPrimaryKey(productId);
        Date date = new Date();
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(products.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        seckillOrderDao.insert(order);
        log.info("===[下单逻辑][创建订单成功]===");
        //扣减库存
        seckillProductsDao.decrStock(productId);
        return BaseResponse.OK;
    }


    public BaseResponse orderV4(SeckillReqV2 req) throws Exception {
        log.info("===[开始调用下单接口（乐观锁+分步式限流+url隐藏+单用户频次限制）]===");
        //校验用户信息、商品信息、库存信息
        log.info("===[校验用户信息、商品信息、库存信息]===");
        BaseResponse paramValidRes = validateParamV4(req.getProductId(), req.getUserId(), req.getVerifyCode());
        if (paramValidRes.getCode() != 0) {
            return paramValidRes;
        }
        log.info("===[校验][通过]===");
        /**
         *  增加应用限流
         *  阻塞式 & 非阻塞式
         */
        log.info("===[开始经过限流程序]===");
        //分布式限流
        try {
            if (!distrubuteLimit.exec()) {
                log.info("你被分布式锁限流了！直接返回失败！");
                return BaseResponse.error(ErrorMessage.SECKILL_RATE_LIMIT_ERROR);
            }
        } catch (IOException e) {
            log.error("===[分布式限流程序发生异常！]===", e);
            return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
        }
        log.info("===[限流程序][通过]===");
        //下单（乐观锁）
        return createOptimisticOrder(req.getProductId(), req.getUserId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse orderV5(SeckillReqV2 req) throws Exception{
        log.info("===[开始调用下单接口（缓存与数据库一致性-先删库存，后更新数据库）]===");
        //校验用户信息、商品信息、库存信息
        log.info("===[校验用户信息、商品信息、库存信息]===");
        BaseResponse paramValidRes = validateParamV4(req.getProductId(), req.getUserId(), req.getVerifyCode());
        if(paramValidRes.getCode() !=0) {
            return paramValidRes;
        }
        log.info("===[校验][通过]===");
        /**
         *  增加应用限流
         *  阻塞式 & 非阻塞式
         */
        log.info("===[开始经过限流程序]===");
        //分布式限流
        try {
            if (!distrubuteLimit.exec()) {
                log.info("你被分布式锁限流了！直接返回失败！");
                return BaseResponse.error(ErrorMessage.SECKILL_RATE_LIMIT_ERROR);
            }
        } catch (IOException e) {
            log.error("===[分布式限流程序发生异常！]===", e);
            return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
        }
        log.info("===[限流程序][通过]===");
        //下单（乐观锁）
        return createOptimisticOrderV5(req.getProductId(), req.getUserId());

    }
    @Override
    public void delStockByCache(Long productId) {
        String key = String.format(Constant.redisKey.SECKILL_SALED_COUNT, productId);
        redisUtil.del(key);
        log.info("===[删除商品id：[{}] 缓存]===", productId);
    }

    private BaseResponse createOptimisticOrderV5(Long productId, Long userId) throws Exception {
        //先删除缓存 再更新数据库
        this.delStockByCache(productId);
        log.info("===[下单逻辑][开始]===");
        // 创建订单
        SeckillProducts products = seckillProductsDao.selectByPrimaryKey(productId);
        Date date = new Date();
        SeckillOrder order = new SeckillOrder();
        order.setProductId(productId);
        order.setProductName(products.getName());
        order.setUserId(userId);
        order.setCreateTime(date);
        seckillOrderDao.insert(order);
        log.info("===[下单逻辑][创建订单成功]===");

        //扣减库存
        int res = seckillProductsDao.updateStockByOptimistic(productId);
        if (res == 0) {
            log.error("===[乐观锁并发更新失败，抛出异常，执行回滚逻辑！]===");
            throw new Exception("乐观锁并发更新失败");
//          return BaseResponse.error(ErrorMessage.SECKILL_FAILED);
        }
        log.info("===[下单逻辑][扣减库存成功]===");
        try {
            addOrderedUserCache(productId, userId);
        } catch (Exception e) {
            log.error("===[记录已购用户缓存时发生异常！]===", e);
        }
        log.info("===[下单逻辑][结束]===");
        return BaseResponse.OK;
    }




    private BaseResponse validateParamV4(Long productId, Long userId, String verifyCode) {
        SeckillProducts product = seckillProductsDao.selectByPrimaryKey(productId);
        if (product == null) {
            log.error("===[产品不存在！]===");
            return BaseResponse.error(ErrorMessage.SYS_ERROR);
        }
        SeckillUser user = seckillUserDao.selectByPrimaryKey(userId);
        if (user == null) {
            log.error("===[用户不存在！]===");
            return BaseResponse.error(ErrorMessage.SYS_ERROR);
        }
        if (product.getStartBuyTime().getTime() > System.currentTimeMillis()) {
            log.error("===[秒杀还未开始！]===");
            return BaseResponse.error(ErrorMessage.SECKILL_NOT_START);
        }
        if (product.getSaled() >= product.getCount()) {
            log.error("===[库存不足！]===");
            return BaseResponse.error(ErrorMessage.STOCK_NOT_ENOUGH);
        }
        if (hasOrderedUserCache(productId, userId)) {
            log.error("===[用户重复下单！]===");
            return BaseResponse.error(ErrorMessage.REPEAT_ORDER_ERROR);
        }
        //单用户访问频次限制
        String visitKey = String.format(nuc.edu.seckill.common.base.Constant.redisKey.SECKILL_USER_VISIT, productId, userId);
        long visitCount = redisUtil.incr(visitKey, 1L);
        redisUtil.expire(visitKey, 5);
        if (visitCount > nuc.edu.seckill.common.base.Constant.VISIT_LIMIT) {
            return BaseResponse.error(ErrorMessage.SECKILL_USER_VISIT_LIMIT_ERROR);
        }
        log.info("===[单用户频次限制合法]===");
        //校验验证码
        String key = String.format(nuc.edu.seckill.common.base.Constant.redisKey.SECKILL_VALIDATE_CODE, productId, userId);
        if (!verifyCode.equals(String.valueOf(redisUtil.get(key)))) {
            return BaseResponse.error(ErrorMessage.SECKILL_VALIDATE_ERROR);
        }
        return BaseResponse.OK;
    }






}
