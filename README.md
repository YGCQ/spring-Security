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
