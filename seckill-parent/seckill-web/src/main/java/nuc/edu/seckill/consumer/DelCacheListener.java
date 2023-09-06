package nuc.edu.seckill.consumer;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.config.MqConfig;
import nuc.edu.seckill.config.MqNode;
import nuc.edu.seckill.model.enums.MqNodesEnum;
import nuc.edu.seckill.model.http.mq.SendMsgReq;
import nuc.edu.seckill.service.SeckillService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 消费监听：删除库存缓存
 *
 * @author: han
 * @since: 2020-07-23 11:47
 **/
@Slf4j
@Component
public class DelCacheListener extends AbstractConsumer {
    @Autowired
    MqConfig mqConfig;

    @Autowired
    @Lazy
    SeckillService seckillService;

    private MqNode mqNode;

    @Override
    public void initMqNode() {
        mqNode = mqConfig.getMqNode(MqNodesEnum.delCache);
        super.topicName = mqNode.getTopic();
        super.groupName = mqNode.getGroup();
        super.tags = mqNode.getTag();
    }

    @Override
    public ConsumeConcurrentlyStatus handleMessage(String msgBody, String msgId) throws Exception {
        SendMsgReq.DelCacheReq vo = JSON.parseObject(msgBody, new TypeReference<SendMsgReq.DelCacheReq>() {
        });
        seckillService.delStockByCache(vo.getProductId());
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    @Override
    public void handleStop() throws Exception {
        log.info("删除缓存重试消息消费.... stop");
    }
}
