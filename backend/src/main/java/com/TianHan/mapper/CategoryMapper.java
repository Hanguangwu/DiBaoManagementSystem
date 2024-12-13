package com.TianHan.mapper;

import com.TianHan.pojo.Category;
import java.util.List;

public interface CategoryMapper {
    List<Category> selectAll();
    Category selectById(int id);
    Category selectByName(String name);
    int insert(Category category);
    int update(Category category);
    int delete(int id);
} 