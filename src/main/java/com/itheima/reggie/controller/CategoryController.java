package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishDto;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category",category);
        categoryService.save(category);
        return R.success("新增成功！");

    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        Page infopage = new Page(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByDesc(Category::getUpdateTime);
        categoryService.page(infopage,queryWrapper);
        return R.success(infopage);
    }
    @PutMapping
    public R<String> upade(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功！");
    }
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.removeBy(ids);
        return R.success("删除成功！");
    }
    //请求网址: http://localhost:8080/category/list?type=1
    //请求方法: GET
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(queryWrapper);
        return R.success(categoryList);
    }

}
