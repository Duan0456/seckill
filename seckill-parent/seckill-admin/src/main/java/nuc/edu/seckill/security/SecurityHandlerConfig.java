package nuc.edu.seckill.security;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.common.base.BaseResponse;
import nuc.edu.seckill.common.exception.ErrorMessage;
import nuc.edu.seckill.common.util.CookieUtils;
import nuc.edu.seckill.common.util.ResponseUtil;
import nuc.edu.seckill.service.AdminTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;


@Configuration
@Slf4j
public class SecurityHandlerConfig {

    @Autowired
    private AdminTokenService tokenService;

    /**
     * 登陆成功，返回Token
     *
     * @return
     */
    @Bean(name = "authenticationSuccessHandler")
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            AdminUser loginUser = (AdminUser) authentication.getPrincipal();
            Token token = tokenService.saveToken(loginUser);
            //此处返回json还是跳转页面，取决于代码本身返回的是json还是jsp等模板
            BaseResponse ok = BaseResponse.ok(token);
            ResponseUtil.responseJson(request, response, HttpStatus.OK.value(), ok);
        };
    }

    @Bean(name = "formAuthenticationSuccessHandler")
    public AuthenticationSuccessHandler formLoginSuccessHandler() {
        return (request, response, authentication) -> {
            AdminUser loginUser = (AdminUser) authentication.getPrincipal();
            Token token = tokenService.saveToken(loginUser);
            //实际项目token要设置超时时间和path,此处我们是模拟，就先不设置了
            CookieUtils.setCookie(response, "token" , token.getToken());
            log.info(JSON.toJSONString(token));
            ResponseUtil.responseRedirectUrl(response, "/product/listPageSeckillProducts");
        };
    }


    /**
     * 登陆失败
     *
     * @return
     */
    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            BaseResponse info;
            log.error("", exception);
            if (exception instanceof BadCredentialsException) {
                info = BaseResponse.error(ErrorMessage.LOGIN_ERROR_USERNAME_PASSWORD_ERROR);
            } else if (exception.getCause() instanceof BaseAuthenticationException) {
                BaseAuthenticationException e = (BaseAuthenticationException) exception.getCause();
                info = e.resp();
            } else if (exception instanceof BaseAuthenticationException) {
                BaseAuthenticationException e = (BaseAuthenticationException) exception;
                info = e.resp();
            } else if (exception instanceof DisabledException) {
                info = BaseResponse.error(ErrorMessage.ACCOUNT_DISABLE);
            } else {
                info = BaseResponse.error(ErrorMessage.LOGIN_ERROR);
            }

            ResponseUtil.responseJson(request, response, HttpStatus.OK.value(), info);
        };
    }

    /**
     * 退出处理
     *
     * @return
     */
    @Bean
    public LogoutSuccessHandler logoutSussHandler() {
        return (request, response, authentication) -> {
            String token = TokenFilter.getToken(request);
            tokenService.deleteToken(token);
            //本地cookie这些还要删除，此处只是模拟，知道就行
            //删除cookie的代码：final Cookie cookie = new Cookie(cookieName, value);
            //		cookie.setMaxAge(0);
            ResponseUtil.responseJson(request, response, HttpStatus.OK.value(), BaseResponse.OK);
        };

    }

    /**
     * 未登录，返回401
     *
     * @return
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.error("", authException);
            BaseResponse resp = BaseResponse.error(ErrorMessage.NO_LOGIN);
            ResponseUtil.responseJson(request, response, HttpStatus.OK.value(), resp);
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.error("", accessDeniedException);
            BaseResponse resp = BaseResponse.error(ErrorMessage.USER_NO_PERMISSION);
            ResponseUtil.responseJson(request, response, HttpStatus.OK.value(), resp);
        };
    }

}
