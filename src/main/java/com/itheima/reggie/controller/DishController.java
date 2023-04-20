package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishDtoService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {




    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    //请求网址: http://localhost:8080/dish/page?page=1&pageSize=10&name=563
    //请求方法: GET
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Dish> Dashpage = new Page(page,pageSize);
        Page<DishDto> DishDtoPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(Dashpage,queryWrapper);
        //获取到dish的所有数据 records
        List<Dish> dishes = Dashpage.getRecords();
        List<DishDto> list = dishes.stream().map((item) -> {
            //对实体类DishDto进行categoryName的设值
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                //设置实体类DishDto的categoryName属性值
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        //对象拷贝  使用框架自带的工具类，第三个参数是不拷贝到属性
        BeanUtils.copyProperties(Dashpage,DishDtoPage,"records");
        DishDtoPage.setRecords(list);
        //因为上面处理的数据没有分类的id,这样直接返回R.success(dishPage)虽然不会报错，但是前端展示的时候这个菜品分类这一数据就为空
        //所以进行了上面的一系列操作
        return R.success(Dashpage);
    }
    //请求网址: http://localhost:8080/dish/1413384757047271425
    //请求方法: GET
    @GetMapping("/{id}")
    public R<DishDto> id(@PathVariable Long id){
        DishDto flavor = dishService.getByIdWithFlavor(id);
        return R.success(flavor);
    }
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto){
      dishService.saveWithFlavor(dishDto);
      return R.success("新增成功！");

    }
    //停售，起售
    //请求网址: http://localhost:8080/dish/status/0?ids=1648218631635042306
    //请求方法: POST
    @PostMapping("/status/{status}")
    public R<String> upade(@PathVariable int status,@RequestParam("ids") List<Long> ids){
        LambdaUpdateWrapper<Dish> queryWrapper = new LambdaUpdateWrapper();
        queryWrapper.set(Dish::getStatus,status).in(Dish::getId,ids);
        dishService.update(queryWrapper);
        return R.success("修改成功！");
    }
    @PutMapping
    public R<String> updateWithFlavor(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功！");
    }
    //批量化删除
    //请求网址: http://localhost:8080/dish?ids=1648601923584524290,1648523471275782145,1413385247889891330
    //请求方法: DELETE
    @DeleteMapping
    public R<String> deleceList(@RequestParam("ids") List<Long> ids){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        dishService.remove(queryWrapper);

        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);
        return R.success("删除成功");
    }
    //    请求网址: http://localhost:8080/dish/list?categoryId=1413341197421846529
    //    请求方法: GET
    @GetMapping("/list")
    public R<List<Dish>> list(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);

    }
}
