package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

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
    public R<List<Category>> list(int type){
        QueryWrapper<Category> wrapper = new QueryWrapper();
        wrapper.eq("type",type).orderByDesc("update_time");
        List<Category> list = categoryService.list(wrapper);
        return R.success(list);
    }

}
