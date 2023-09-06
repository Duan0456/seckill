package nuc.edu.seckill.service.impl;

import cn.hutool.core.lang.Assert;
import nuc.edu.seckill.dao.SeckillAdminDao;
import nuc.edu.seckill.model.SeckillAdmin;
import nuc.edu.seckill.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("adminService")
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private SeckillAdminDao seckillAdminDao;

    @Override
    public List<SeckillAdmin> listAdmin() {
        SeckillAdmin item = new SeckillAdmin();
        return seckillAdminDao.list(item);
    }

    @Override
    public SeckillAdmin findByUsername(String username) {
        Assert.notNull(username);
        SeckillAdmin item = new SeckillAdmin();
        item.setLoginName(username);
        List<SeckillAdmin> list = seckillAdminDao.list(item);
        if (!CollectionUtils.isEmpty(list)) {
            Assert.isTrue(list.size() == 1);
            return list.get(0);
        }
        return null;
    }
}
