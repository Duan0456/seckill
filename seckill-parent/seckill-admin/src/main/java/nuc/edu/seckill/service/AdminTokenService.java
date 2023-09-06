package nuc.edu.seckill.service;

import nuc.edu.seckill.security.AdminUser;
import nuc.edu.seckill.security.AdminUser;
import nuc.edu.seckill.security.Token;

public interface AdminTokenService {
    Token saveToken(AdminUser loginUser);

    void deleteToken(String token);

    AdminUser getLoginUser(String token);

    void refresh(AdminUser loginUser);
}
