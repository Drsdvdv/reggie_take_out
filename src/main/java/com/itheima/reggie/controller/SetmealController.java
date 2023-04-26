package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.Dto.SetmealDto;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;



    //请求网址: http://localhost:8080/setmeal/page?page=1&pageSize=10
    //请求方法: GET
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> Page = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(Page,wrapper);
        //属性对拷
        BeanUtils.copyProperties(Page,dtoPage,"records");
        List<Setmeal> records = Page.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }
    //请求网址: http://localhost:8080/setmeal
    //请求方法: POST
    @PostMapping
    public R<String> setmeal(@RequestBody Setmeal setmeal){
        setmealService.save(setmeal);
        return R.success("添加成功！");
    }
    //http://localhost:8080/setmeal/1648878918616870913
    //请求方法: GET
    @GetMapping("{id}")
    public R<SetmealDto> get(@PathVariable("id") Long id) {
        SetmealDto date = setmealService.getDate(id);
        return R.success(date);
    }
    //请求网址: http://localhost:8080/setmeal
    //请求方法: PUT
    @PutMapping
    public R<String> Put(@RequestBody SetmealDto setmealDto) {
        if (setmealDto == null) {
            return R.error("请求异常");
        }

        if (setmealDto.getSetmealDishes() == null) {
            return R.error("套餐没有菜品,请添加套餐");
        }
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealId);
        setmealDishService.remove(queryWrapper);

        //为setmeal_dish表填充相关的属性
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }
        //批量把setmealDish保存到setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
        setmealService.updateById(setmealDto);

        return R.success("套餐修改成功");
    }
        //请求网址: http://localhost:8080/setmeal/status/0?ids=1648878918616870913
    //请求方法: POST
    @PostMapping("/status/{status}")
    public R<String> Updadeids(@PathVariable int status,@RequestParam("ids") List<Long> ids){
        LambdaUpdateWrapper<Setmeal> UpdateWrapper = new LambdaUpdateWrapper();
        UpdateWrapper.set(Setmeal::getStatus,status).in(Setmeal::getId,ids);
        setmealService.update(UpdateWrapper);
        return R.success("修改成功！！");
    }
    //请求网址: http://localhost:8080/setmeal?ids=1648878918616870913,1415580119015145474
    //请求方法: DELETE
    @DeleteMapping
    public R<String> delect(@RequestParam("ids") List<Long> ids){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper();
        wrapper.in(Setmeal::getId,ids);
        setmealService.remove(wrapper);
        return R.success("删除成功");
    }
    //请求网址: http://localhost:8080/setmeal/list?categoryId=1648874631539032066&status=1
    //请求方法: GET
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }
}
