package com.yg.security.pojo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class User implements UserDetails {

    private Integer id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean locked;
    private List<Role> roles;

    //获取当前用户对象所具有的角色信息
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities=new ArrayList<>();
        for (Role role:roles)  {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    //获取当前用户对象密码
    @Override
    public String getPassword() {
        return password;
    }

    //获取当前对象的用户名
    @Override
    public String getUsername() {
        return username;
    }

    //当前账户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //当前账户是否未锁定
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    //当前账户密码是否未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //当前账户是否可用
    @Override
    public boolean isEnabled() {
        return enabled;
    }


}
