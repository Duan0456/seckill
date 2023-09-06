package nuc.edu.seckill.config;


import lombok.Data;
import nuc.edu.seckill.model.enums.MqNodesEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author: han
 * @since: 2020-07-22 20:00
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = MqConfig.PREFIX)
@EnableConfigurationProperties(MqConfig.class)
public class MqConfig {
    public static final String PREFIX = "rocketmq.config";
    /**
     * 缓存实例配置
     */
    public Map<String, MqNode> nodes;

    public MqNode getMqNode(MqNodesEnum mqNodesEnum) {
        return nodes.get(mqNodesEnum.getName());
    }
}
