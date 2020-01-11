package com.yg.security.Config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {

    //判断当前登录的用户是否具备当前请求URL所需要的角色信息，不具备抛出AccessDeniedException异常,否则不做任何事即可
    @Override
    public void decide(Authentication auth,Object object, Collection<ConfigAttribute> ca){
        Collection<? extends GrantedAuthority> auths = auth.getAuthorities();
        for (ConfigAttribute configAttribute : ca) {
            if ("ROLE_LOGIN".equals(configAttribute.getAttribute()) && auth instanceof UsernamePasswordAuthenticationToken) {
                return;
            }
            for (GrantedAuthority authority : auths) {
                if (configAttribute.getAttribute().equals(authority.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
