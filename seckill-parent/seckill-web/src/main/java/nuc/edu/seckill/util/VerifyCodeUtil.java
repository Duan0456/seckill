package nuc.edu.seckill.util;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class VerifyCodeUtil {
    private static Properties props = new Properties();

    @Bean
    public DefaultKaptcha defaultKaptcha() throws Exception {
        // 创建DefaultKaptcha对象
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();

        // 读取配置文件
        try {
            props.load(VerifyCodeUtil.class.getClassLoader()
                    .getResourceAsStream("kaptcha.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 将Properties文件设到DefaultKaptcha对象中
        defaultKaptcha.setConfig(new Config(props));
        return defaultKaptcha;
    }

}
