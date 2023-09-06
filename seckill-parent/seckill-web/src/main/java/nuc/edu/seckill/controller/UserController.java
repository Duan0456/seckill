package nuc.edu.seckill.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.common.base.BaseRequest;
import nuc.edu.seckill.common.base.BaseResponse;
import nuc.edu.seckill.common.exception.ErrorMessage;
import nuc.edu.seckill.common.model.CommonWebUser;
import nuc.edu.seckill.model.SeckillUser;
import nuc.edu.seckill.model.http.UserReq;
import nuc.edu.seckill.model.http.UserResp;
import nuc.edu.seckill.security.WebUserUtil;
import nuc.edu.seckill.service.UserService;
import nuc.edu.seckill.util.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final String USER_PHONE_CODE_BEFORE = "u:p:c:b:";//前缀的一种通用写法

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/getPhoneSmsCode")//获取验证码
    public BaseResponse<Boolean> getPhoneSmsCode(@Valid @RequestBody BaseRequest<UserReq.BaseUserInfo> req) {
        String phone = req.getData().getPhone();
        SeckillUser seckillUser = userService.findByPhone(phone);
        //判断用户是否存在
        if (seckillUser != null) {
            //短信验证码
            String randomCode = "123456";//此处正常是调用第三方接口
            //验证码存储在redis中，方便后续判断
            redisUtil.set(USER_PHONE_CODE_BEFORE + phone, randomCode, 60 * 30);
            return BaseResponse.OK(true);
        } else {
            return BaseResponse.OK(false);
        }
    }

    @PostMapping("/userPhoneLogin")//
    public BaseResponse userPhoneLogin(@Valid @RequestBody BaseRequest<UserReq.LoginUserInfo> req) {
        UserReq.LoginUserInfo loginInfo = req.getData();
        Object existObj = redisUtil.get(USER_PHONE_CODE_BEFORE + loginInfo.getPhone());
        if (existObj == null || !existObj.toString().equals(loginInfo.getSmsCode())) {
            return BaseResponse.error(ErrorMessage.SMSCODE_ERROR);
        } else {
            redisUtil.del(USER_PHONE_CODE_BEFORE + loginInfo.getPhone());
            SeckillUser seckillUser = userService.findByPhone(loginInfo.getPhone());
            CommonWebUser commonWebUser = new CommonWebUser();
            BeanUtils.copyProperties(seckillUser, commonWebUser);
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            //设置token超时时间为1个月，实际根据需求确定
            redisUtil.set(token, JSON.toJSONString(commonWebUser), 60 * 60 * 30 * 24);
            UserResp.BaseUserResp resp = new UserResp.BaseUserResp();
            resp.setToken(token);
            return BaseResponse.OK(resp);
        }
    }

    //演示用法
    @GetMapping("/checkUserToken")
    public void checkUserToken() {
        CommonWebUser commonWebUser = WebUserUtil.getLoginUser();
        log.info(JSON.toJSONString(commonWebUser));
    }

}
