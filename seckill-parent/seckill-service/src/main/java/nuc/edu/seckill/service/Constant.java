package nuc.edu.seckill.service;

public interface Constant {
    String FAIL = "FAIL";
    String SUCCESS = "SUCCESS";
    interface redisKey {
        /**
         * 分布式锁的KEY
         * sk:d:lock:商品id
         */
        String SECKILL_DISTRIBUTED_LOCK = "sk:d:lock:%s";
        /**
         * 商品id的缓存库存数量
         * * key sk:sc:商品id
         */
        String SECKILL_SALED_COUNT = "sk:sc:%s";
        /**
         * 已购买用户名单 + 商品id
         * sk:ou:p:商品id
         */
        String SECKILL_ORDERED_USER = "sk:ou:p:%s";
    }


}
