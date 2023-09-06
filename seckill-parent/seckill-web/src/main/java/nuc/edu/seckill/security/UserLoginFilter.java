package nuc.edu.seckill.security;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.common.base.BaseResponse;
import nuc.edu.seckill.common.exception.ErrorMessage;
import nuc.edu.seckill.common.model.CommonWebUser;
import nuc.edu.seckill.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 验证用户是否已经登录，需要验证的接口必须登录.
 */
@Slf4j
@WebFilter(filterName = "userLoginFilter", urlPatterns = "/*")
public class UserLoginFilter implements Filter {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${auth.login.pattern}")
    private String urlPattern;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        String url = request.getRequestURI();
        log.info("url:=" + url + " ,pattern:=" + urlPattern);
        if (url.matches(urlPattern)) {
            if (session.getAttribute(WebUserUtil.SESSION_WEBUSER_KEY) != null) {
                filterChain.doFilter(request, response);
                return;
            } else {
                //token我们此处约定保存在http协议的header中，也可以保存在cookie中，
                //调用我们接口的前端或客户端也会保存cookie，具体使用方式由公司确定
                String tokenValue = request.getHeader("token");
                if (StringUtils.isNotEmpty(tokenValue)) {
                    Object object = redisUtil.get(tokenValue);
                    if (object != null) {
                        CommonWebUser commonWebUser = JSONObject.parseObject(object.toString(), CommonWebUser.class);
                        session.setAttribute(WebUserUtil.SESSION_WEBUSER_KEY, commonWebUser);
                        filterChain.doFilter(request, response);
                        return;
                    } else {
                        //返回接口调用方需要登录的错误码，接口调用方开始登录
                        returnJson(response);
                        return;
                    }
                } else {
                    //返回接口调用方需要登录的错误码，接口调用方开始登录
                    returnJson(response);
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
        return;
    }


    public void returnJson(ServletResponse response) {
        PrintWriter pw = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            pw = response.getWriter();
            BaseResponse baseResponse = new BaseResponse(ErrorMessage.USER_NEED_LOGIN.getCode(), ErrorMessage.USER_NEED_LOGIN.getMessage(), null);
            pw.print(JSON.toJSONString(baseResponse));
        } catch (IOException e) {
            log.error("response error", e);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}
