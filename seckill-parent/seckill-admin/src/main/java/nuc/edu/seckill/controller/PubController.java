package nuc.edu.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 登录界面等不需要验证登录的页面.
 */
@Controller
@RequestMapping(value = "/pub")
@Slf4j
public class PubController {
    @RequestMapping("/beforeLogin")
    public String beforeLogin() {
        return "pub/beforeLogin";
    }
}
