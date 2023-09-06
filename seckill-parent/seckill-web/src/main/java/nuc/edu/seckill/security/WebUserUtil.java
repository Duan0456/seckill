package nuc.edu.seckill.security;

import com.alibaba.fastjson.JSONObject;
import nuc.edu.seckill.common.model.CommonWebUser;
import nuc.edu.seckill.util.RedisUtil;
import nuc.edu.seckill.util.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebUserUtil {
    public static final String SESSION_WEBUSER_KEY = "web_user_key";

    /**
     * 获取当前用户
     * @return
     */
    public static CommonWebUser getLoginUser() {
        // 获取相关对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        HttpSession session = request.getSession();

        CommonWebUser commonWebUser = null;

        if (session. getAttribute(SESSION_WEBUSER_KEY) != null) {
            commonWebUser = (CommonWebUser) session.getAttribute(SESSION_WEBUSER_KEY);
        } else {
            RedisUtil redisUtil = SpringContextHolder.getBean("redisUtil");
            String token = request.getHeader("token");
            if (StringUtils.isNotEmpty(token)) {//不单独获取是防止空指针
                Object object = redisUtil.get(token);
                if (object != null) {
                    commonWebUser = JSONObject.parseObject(object.toString(),CommonWebUser.class);
                    session.setAttribute(SESSION_WEBUSER_KEY,commonWebUser);
                }
            }
        }
        return commonWebUser;
    }
}
