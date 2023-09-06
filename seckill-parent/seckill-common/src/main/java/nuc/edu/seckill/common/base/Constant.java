package nuc.edu.seckill.common.base;


public interface Constant {
    String FAIL = "FAIL";
    String SUCCESS = "SUCCESS";

    String VALIDATE_CODE_SALT = "bmw384919";

    int VISIT_LIMIT = 1;

    interface redisKey {
        /**
         * 分布式锁的KEY
         * sk:d:lock:商品id
         */
        String SECKILL_DISTRIBUTED_LOCK = "sk:d:lock:%s";


        /**
         * 缓存库存数量 + 商品id
         * sk:sc:商品id
         */
        String SECKILL_SALED_COUNT = "sk:sc:%s";

        /**
         * 已购买用户名单 + 商品id
         * sk:ou:p:商品id
         */
        String SECKILL_ORDERED_USER = "sk:ou:p:%s";

        /**
         * 秒杀验证码
         * sk:p:商品id:u:用户id
         */
        String SECKILL_VALIDATE_CODE = "sk:p:%s:u:%s";

        /**
         * 图片验证码（1分钟过期）
         * sk:i:{ImageId}
         */
        String SECKILL_IMAGE_CODE = "sk:i:%s";


        /**
         * 访问用户名单 + 商品id + 用户id
         * sk:v:p:商品id:u:用户id
         */
        String SECKILL_USER_VISIT = "sk:v:p:%s:u:%s";
    }

}
