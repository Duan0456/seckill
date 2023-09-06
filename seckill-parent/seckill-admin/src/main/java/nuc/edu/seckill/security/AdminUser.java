package nuc.edu.seckill.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class AdminUser implements UserDetails {


    private Long id;

    private String username;

    private String password;

    private boolean enable;

    private String roles;

    private Date createDate;

    private Date modifyDate;

    @Transient
    private List<GrantedAuthority> authorities;


    private String token;

    /**
     * 登陆时间戳（毫秒）
     */
    private Long loginTime;
    /**
     * 过期时间戳
     */
    private Long expireTime;

    //通过用户名查询到的密码 密码肯定是加密过的 这里明文密码是 123456
//    String password = "e10adc3949ba59abbe56e057f20f883e";
//    //用户对应权限
//    List<Role> roles = Lists.newArrayList(new Role(1L, "教师"), new Role(2L, "学生"));


    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //这个本身是对应roles字段的，但是因为结构不一致，所以重新创建一个，后续补充这部分
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
