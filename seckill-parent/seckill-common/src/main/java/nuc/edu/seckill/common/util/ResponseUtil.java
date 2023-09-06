package nuc.edu.seckill.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import nuc.edu.seckill.common.exception.ErrorMessage;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {

    public static void responseJson(HttpServletRequest request, HttpServletResponse response, int status, Object data) {
        try {
            String origin = request.getHeader("Origin");
            if (StringUtils.isBlank(origin)) {
                origin = "*";
            }
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(status);

            response.getWriter().write(JSONObject.toJSONString(data, SerializerFeature.WriteMapNullValue));
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetResponseJson(int status, Object data, HttpServletRequest request, HttpServletResponse response) {
        response.reset();
        responseJson(request, response, status, data);
    }

    public static void responseRedirectUrl(HttpServletResponse response, String url) {
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            throw new RuntimeException(ErrorMessage.SYS_ERROR.toString());
        }
    }
}
