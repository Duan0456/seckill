package nuc.edu.seckill.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 分布式限流工具
 *
 * @author: han
 * @since: 2020-11-12 16:01
 **/
@Component
public class DistrubuteLimit {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private DefaultRedisScript<Long> getRedisScript;

    private static final String LIMIT_LUA = "local times = redis.call('incr', KEYS[1])\n" +
            "if times == 1 then\n" +
            "    redis.call('expire', KEYS[1], ARGV[1])\n" +
            "end\n" +
            "if times > tonumber(ARGV[2]) then\n" +
            "    return 0\n" +
            "end\n" +
            "return 1";

    @PostConstruct
    public void init() {
        getRedisScript = new DefaultRedisScript<Long>();
        getRedisScript.setResultType(Long.class);
        getRedisScript.setScriptText(LIMIT_LUA);
    }

    public boolean exec() throws IOException {
        List<String> keyList = new ArrayList();
        String key = "ip:" + System.currentTimeMillis() / 1000; //此处将当前时间戳取秒数
        //String key = "ip:" + 1; //此处硬编码时间，保证请求都是在同一秒内发起
        keyList.add(key);
        return redisTemplate.execute(getRedisScript, keyList, 3000, 6) == 1 ? true : false;
    }
}
