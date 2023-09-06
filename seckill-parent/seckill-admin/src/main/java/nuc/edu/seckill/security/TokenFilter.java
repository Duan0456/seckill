package nuc.edu.seckill.security;


import nuc.edu.seckill.common.util.CookieUtils;
import nuc.edu.seckill.service.AdminTokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_KEY = "token";

    @Autowired
    private AdminTokenService tokenService;

    @Autowired
    private MyUserDetailsService userDetailsService;


    private static final Long MINUTES_30 = 30 * 60 * 1000L;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getToken(request);
        if (StringUtils.isNotBlank(token)) {
            AdminUser loginUser = tokenService.getLoginUser(token);
            if (loginUser != null) {
                loginUser = checkLoginTime(loginUser);
                
                // 校验IP地域 start
//                String requestsIp = IPUtil.getIpAddress(request);
//                if (IpCheckUtil.checkIp(ipTableService.getList(), loginUser.getUsername(), requestsIp) == false) {
//                    ErrorMessage error = ErrorMessage.UN_USERNAME_STATUS;
//                    error.setMessage("登录账号ip区域不允许, 您的IP："+requestsIp);
//                    throw BaseAuthenticationException.error(error);
//                }
                // 校验IP地域 end

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser,
                        null, loginUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 校验时间<br>
     * 过期时间与当前时间对比，临近过期30分钟内的话，自动刷新缓存
     *
     * @param loginUser
     * @return
     */
    private AdminUser checkLoginTime(AdminUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MINUTES_30) {
            String token = loginUser.getToken();
            loginUser = (AdminUser) userDetailsService.loadUserByUsername(loginUser.getUsername());
            loginUser.setToken(token);
            tokenService.refresh(loginUser);
        }
        return loginUser;
    }

    /**
     * 根据参数或者header获取token
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getParameter(TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            token = request.getHeader(TOKEN_KEY);
            if (StringUtils.isBlank(token)) {
                token = CookieUtils.getValue(request, TOKEN_KEY);
            }
        }

        return token;
    }

}
