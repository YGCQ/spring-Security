package com.yg.security.Mapper;

import com.yg.security.pojo.Role;
import com.yg.security.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User loadUserByUsername(String name);
    List<Role> getUserRolesByUid(Integer id);
}
