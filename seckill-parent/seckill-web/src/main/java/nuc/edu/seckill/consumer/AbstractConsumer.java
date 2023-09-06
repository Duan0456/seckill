package nuc.edu.seckill.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

/**
 * RocketMQ消费者抽象类
 *
 * @author: han
 * @since: 2020-07-22 17:01
 **/
@Slf4j
public abstract class AbstractConsumer {
    protected boolean handleMsg = true;
    protected String topicName;
    protected String groupName;
    protected String tags;

    protected MessageModel messageModel = MessageModel.CLUSTERING;
    // String instanceName;
    protected DefaultMQPushConsumer consumer = null;
    @Value("${rocketmq.consumer.namesrvAddr}")
    private String rocketmqAddress = "";
    @Value("${rocketmq.consumer.consumeMessageBatchMaxSize}")
    private int consumeMessageBatchMaxSize;


    @PostConstruct
    public void start() {
        initMqNode();
        log.info("topic={}, groupName={},address={} start", topicName, groupName, rocketmqAddress);
        try {
            consumer = new DefaultMQPushConsumer(groupName);
            consumer.setNamesrvAddr(rocketmqAddress);// MQ地址
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            // consumer.setInstanceName(instanceName);//实例名称
            consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
            consumer.subscribe(topicName, tags);
            //广播与队列
            consumer.setMessageModel(messageModel);

            // 注册监听
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext
                        context) {
                    for (int i = 0; i < msgs.size(); i++) {
                        if (handleMsg) {
                            MessageExt msgExt = msgs.get(i);
                            String msgId = msgExt.getMsgId();
                            String keys = msgExt.getKeys();
                            String msgBody = new String(msgExt.getBody());
                            log.info("===Consumer Receive message[msgId={},keys={}],[topicName={},tags={},group={}," +
                                            "reConsumeTimes={}]," +
                                            "[body={}]===",
                                    msgId, msgExt.getKeys(),
                                    topicName,
                                    tags, groupName, msgExt.getReconsumeTimes(), msgBody);
                            try {
                                log.info("===[开始消费group={}的消息：msgBody={},msgId={},keys={}]===", groupName, msgBody,
                                        msgId, keys);
                                handleMessage(msgBody, msgId);
                                log.info("===[结束消费][group={}的消息：msgBody={},msgId={},keys={}]===", groupName, msgBody,
                                        msgId, keys);
                            } catch (Throwable e) {
                                log.error("consumer error:message[msgId={},keys={}],[topic={}, groupName={}, " +
                                                "data={}]," +
                                                "[exception:{}]", msgId, msgExt.getKeys(), topicName,
                                        groupName, msgBody, e);
                                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                            }
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            consumer.start();
        } catch (Exception e) {
            log.error("topic={}, groupName={} started error", topicName,
                    groupName, e);
        }
        log.info("topic={}, groupName={} started", topicName,
                groupName);
    }

    @PreDestroy
    public void stop() {
        setHandleMsg(false);
        if (null != this.consumer) {
            this.consumer.shutdown();
        }
        try {
            handleStop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info("topic={},groupName={} stoped", topicName,
                groupName);
    }

    public abstract ConsumeConcurrentlyStatus handleMessage(String message, String msgId) throws
            Exception;

    public abstract void handleStop() throws Exception;

    public abstract void initMqNode();

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public void setHandleMsg(boolean handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

}

