package nuc.edu.seckill.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: han
 * @since: 2020-07-22 20:05
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqNode {
    private String topic;
    private String tag;
    private String group;
}
