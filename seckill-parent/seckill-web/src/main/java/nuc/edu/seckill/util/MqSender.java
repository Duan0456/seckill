package nuc.edu.seckill.util;

import com.alibaba.fastjson.JSON;
import nuc.edu.seckill.config.MqConfig;
import nuc.edu.seckill.config.MqNode;
import nuc.edu.seckill.model.enums.MqNodesEnum;
import nuc.edu.seckill.model.http.mq.SendMsgReq;
import nuc.edu.seckill.producer.RocketProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqSender {

    @Autowired
    RocketProducer rocketProducer;

    @Autowired
    MqConfig mqConfig;


    public void sendDelCacheMsg(SendMsgReq.DelCacheReq req) {
        MqNode mqNode = mqConfig.getMqNode(MqNodesEnum.delCache);
        rocketProducer.sendMsg(mqNode, "productId".concat(req.getProductId().toString()), JSON.toJSONString(req));
    }

    public void sendOrderMsg(SendMsgReq.OrderReq req) {
        MqNode mqNode = mqConfig.getMqNode(MqNodesEnum.order);
        rocketProducer.sendMsg(mqNode, "productId".concat(req.getProductId().toString()).concat("userId").concat(req
                .getUserId().toString()), JSON.toJSONString(req));
    }
}
