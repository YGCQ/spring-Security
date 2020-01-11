# Spring Security安全认证

---

## 1.数据表设计

分别是用户表(user)、角色表(role)、用户关系表(user_role)、资源表(menu)和资源角色关联表(menu_role)

![image-20200111224422202](E:\笔记\Spring\image-20200111224422202.png)

## 部分代码解释

### 角色继承

 各个角色之间不存在任何关系，但一般来说角色之前是有关系的，例如**ROLE_admin**一般既有**管理员**的权限，又具有**用户**权限。

```java
//设置层级关系
    @Bean
    RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl roleHierarchy=new RoleHierarchyImpl();
        String hierarchy = "ROLE_dba > ROLE_admin ROLE_admin > ROLE_user";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
```



参考文档： https://www.hangge.com/blog/cache/detail_2677.html 

## 基于数据库的URL权限规则配置

### 自定义 FilterInvocationSecurityMetadataSource
要实现动态配置权限，首先需要自定义 FilterInvocationSecurityMetadataSource：
注意：自定义 FilterInvocationSecurityMetadataSource 主要实现该接口中的 getAttributes 方法，该方法用来确定一个请求需要哪些角色。

```java
@Component
public class CustomFilterInvocationSecurityMetadataSource
        implements FilterInvocationSecurityMetadataSource {
 
    // 创建一个AnipathMatcher，主要用来实现ant风格的URL匹配。
    AntPathMatcher antPathMatcher = new AntPathMatcher();
 
    @Autowired
    MenuMapper menuMapper;
 
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object)
            throws IllegalArgumentException {
        // 从参数中提取出当前请求的URL
        String requestUrl = ((FilterInvocation) object).getRequestUrl();
 
        // 从数据库中获取所有的资源信息，即本案例中的menu表以及menu所对应的role
        // 在真实项目环境中，开发者可以将资源信息缓存在Redis或者其他缓存数据库中。
        List<Menu> allMenus = menuMapper.getAllMenus();
 
        // 遍历资源信息，遍历过程中获取当前请求的URL所需要的角色信息并返回。
        for (Menu menu : allMenus) {
            if (antPathMatcher.match(menu.getPattern(), requestUrl)) {
                List<Role> roles = menu.getRoles();
                String[] roleArr = new String[roles.size()];
                for (int i = 0; i < roleArr.length; i++) {
                    roleArr[i] = roles.get(i).getName();
                }
                return SecurityConfig.createList(roleArr);
            }
        }
 
        // 如果当前请求的URL在资源表中不存在相应的模式，就假设该请求登录后即可访问，即直接返回 ROLE_LOGIN.
        return SecurityConfig.createList("ROLE_LOGIN");
    }
 
    // 该方法用来返回所有定义好的权限资源，Spring Security在启动时会校验相关配置是否正确。
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        // 如果不需要校验，那么该方法直接返回null即可。
        return null;
    }
 
    // supports方法返回类对象是否支持校验。
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}

原文出自：www.hangge.com  转载请保留原文链接：https://www.hangge.com/blog/cache/detail_2676.html
```

### 自定义 FilterInvocationSecurityMetadataSource 
 当一个请求走完 FilterInvocationSecurityMetadataSource 中的 getAttributes 方法后，接下来就会来到 AccessDecisionManager 类中进行角色信息的对比，自定义 AccessDecisionManager 代码如下：
 ```java
 @Component
public class CustomAccessDecisionManager
        implements AccessDecisionManager {
 
    // 该方法判断当前登录的用户是否具备当前请求URL所需要的角色信息
    @Override
    public void decide(Authentication auth,
                       Object object,
                       Collection<ConfigAttribute> ca){
        Collection<? extends GrantedAuthority> auths = auth.getAuthorities();
 
        // 如果具备权限，则不做任何事情即可
        for (ConfigAttribute configAttribute : ca) {
            // 如果需要的角色是ROLE_LOGIN，说明当前请求的URL用户登录后即可访问
            // 如果auth是UsernamePasswordAuthenticationToken的实例，说明当前用户已登录，该方法到此结束
            if ("ROLE_LOGIN".equals(configAttribute.getAttribute())
                    && auth instanceof UsernamePasswordAuthenticationToken) {
                return;
            }
 
            // 否则进入正常的判断流程
            for (GrantedAuthority authority : auths) {
                // 如果当前用户具备当前请求需要的角色，那么方法结束。
                if (configAttribute.getAttribute().equals(authority.getAuthority())) {
                    return;
                }
            }
        }
 
        // 如果不具备权限，就抛出AccessDeniedException异常
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
 ```
 
 参考文档：https://www.hangge.com/blog/cache/detail_2676.html
