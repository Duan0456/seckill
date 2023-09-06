package nuc.edu.seckill.producer;

import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.config.MqNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * rocketMQ生产者
 *
 * @author: han
 * @since: 2020-07-22 16:13
 **/
@Slf4j
@Component
public class RocketProducer {

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    /**
     * 发送普通消息
     *
     * @param msg
     * @return
     * @throws Exception
     */
    public SendResult sendMsg(MqNode mqNode, String key, String msg) {
        log.info("【消息发送】topic:{},tag:{},key={},msg:{}", mqNode.getTopic(), mqNode.getTag(), key, msg);
        if (StringUtils.isBlank(key)) {
            key = mqNode.getTag();
        }
        return this.sendMsg(mqNode.getTopic(), mqNode.getTag(), key, msg);
    }

    /**
     * 发送普通消息
     *
     * @param tag
     * @param msg
     * @return
     * @throws Exception
     */
    public SendResult sendMsg(String topic, String tag, String key, String msg) {
        try {
            Message rocketMsg = new Message(topic, tag, key, msg.getBytes("UTF-8"));
            SendResult sendResult = defaultMQProducer.send(rocketMsg);
            if (sendResult != null) {
                log.info("发送消息成功，发送内容：topic:{},key={},tag:{}", topic, key, tag);
            } else {
                log.info("发送消息失败，发送内容：topic:{},tag:{},key={},msg={}", topic, tag, key, msg);
            }
            return sendResult;
        } catch (Exception e) {
            log.error("===[发送消息异常：excp={}]===", e);
        }
        return null;

    }


    @PostConstruct
    public void init() throws MQClientException {
        this.defaultMQProducer.start();
    }

    @PreDestroy
    public void destroy() {
        this.defaultMQProducer.shutdown();
    }

}
