package nuc.edu.seckill.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cookie工具类.
 *
 * @author dangdang
 *
 */
public abstract class CookieUtils {

    private CookieUtils() {

    }

    private static Logger log = LoggerFactory.getLogger(CookieUtils.class);

    public static void clearCookie(final HttpServletRequest request,
                                   final HttpServletResponse response, final String cookieName) {
        final Cookie[] cookies = request.getCookies();
        try {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    cookies[i].setMaxAge(0);
                    cookies[i].setPath("/");
                    response.addCookie(cookies[i]);
                }
            }
        } catch (final Exception ex) {
            log.error("清空Cookies发生异常！", ex);
        }
    }

    public static Cookie getCookie(final HttpServletRequest request, final String cookieName) {
        final Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) {
            return null;
        }
        for (final Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public static String getValue(final HttpServletRequest request, final String cookieName) {
        final Cookie cookie = getCookie(request, cookieName);
        if (cookie == null) {
            return null;
        }
        return cookie.getValue();
    }

    /**
     * 设置cookie, 根路径, 会员期间有效.
     */
    public static void setCookie(final HttpServletResponse response,
                                 final String cookieName, final String value) {
        setCookie(response, "/", -1, cookieName, value);
    }

    /**
     * 设置cookie.
     */
    public static void setCookie(final HttpServletResponse response,
                                 final String path, final int maxAge,
                                 final String cookieName, final String value) {
        final Cookie cookie = new Cookie(cookieName, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        response.addCookie(cookie);
    }
}