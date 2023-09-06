package nuc.edu.seckill.service;

import nuc.edu.seckill.model.SeckillUser;

public interface UserService {
    SeckillUser findByPhone(String phone);
}
