package com.yg.security.Mapper;

import com.yg.security.pojo.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {
    List<Menu> getAllMenus();
}
