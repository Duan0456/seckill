package nuc.edu.seckill.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 生产者配置类
 *
 * @author: han
 * @since: 2020-07-22 16:41
 **/
@Configuration
@Slf4j
public class ProducerConfig {

    @Value("${rocketmq.producer.namesrvAddr}")
    private String namesrvAddr;
    /**
     * 消息发送超时时间，默认3秒
     */
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private String sendMsgTimeout;


    @Bean
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer("defaultProducer");
        producer.setNamesrvAddr(this.namesrvAddr);
        producer.setSendMsgTimeout(Integer.valueOf(sendMsgTimeout));
        return producer;
    }
}
