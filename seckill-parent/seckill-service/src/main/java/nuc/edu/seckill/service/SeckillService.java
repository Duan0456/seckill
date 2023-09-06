package nuc.edu.seckill.service;

import nuc.edu.seckill.common.base.BaseResponse;
import nuc.edu.seckill.model.http.SeckillReq;
import nuc.edu.seckill.model.http.SeckillReqV2;

public interface SeckillService {
    BaseResponse sOrder(SeckillReq req);
    BaseResponse pOrder(SeckillReq req);
    BaseResponse oOrder(SeckillReq req) throws Exception;
    BaseResponse redissonOrder(SeckillReq req);
    BaseResponse cOrder(SeckillReq req) throws Exception;
    BaseResponse orderV1(SeckillReq req) throws Exception;
    BaseResponse createOrder(SeckillReq req);
    BaseResponse orderV4(SeckillReqV2 req) throws Exception;
    BaseResponse orderV5(SeckillReqV2 req) throws Exception;
    void delStockByCache(Long productId);
}
