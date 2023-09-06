package nuc.edu.seckill.service.impl;


import nuc.edu.seckill.dao.DemoDao;
import nuc.edu.seckill.model.Demo;
import nuc.edu.seckill.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoServiceImpl implements DemoService {

    @Autowired
    private DemoDao demoDao;

    @Override
    public List<Demo> list() {
        return demoDao.list(new Demo());
    }
}
