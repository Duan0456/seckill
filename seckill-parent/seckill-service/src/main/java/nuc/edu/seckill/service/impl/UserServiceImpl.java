package nuc.edu.seckill.service.impl;

import lombok.extern.slf4j.Slf4j;
import nuc.edu.seckill.dao.SeckillUserDao;
import nuc.edu.seckill.model.SeckillUser;
import nuc.edu.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.core.lang.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private SeckillUserDao seckillUserDao;

    @Override
    public SeckillUser findByPhone(String phone) {

        Assert.notNull(phone, "手机号不能为空");
        SeckillUser seckillUser = new SeckillUser();
        seckillUser.setPhone(phone);
        List<SeckillUser> list = seckillUserDao.list(seckillUser);
        if(!CollectionUtils.isEmpty(list)) {
        Assert.isTrue(list.size() == 1);
            return list.get(0);
        }
        return null;
    }
}
