package nuc.edu.seckill.controller;


import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.model.SeckillAdmin;
import nuc.edu.seckill.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(value = "/admin")
@Slf4j
public class SeckillAdminController {
    @Autowired
    private IAdminService adminService;
    @RequestMapping("/listAdminPage")
    public String listAdminPage(Model model) {
        List<SeckillAdmin> list = adminService.listAdmin();
        model.addAttribute("list", list);
        return "admin/listAdminPage";
    }
}
