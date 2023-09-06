package nuc.edu.seckill.model.http.mq;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 发送MQ消息的请求
 *
 * @author: han
 * @since: 2020-07-24 17:19
 **/
@Data
public class SendMsgReq implements Serializable {


    @Data
    @Accessors(chain = true)
    public static class DelCacheReq implements Serializable {
        private Long productId;
    }

    @Data
    @Accessors(chain = true)
    public static class OrderReq implements Serializable {
        private Long productId;
        private Long userId;
    }

}