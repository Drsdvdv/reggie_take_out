package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    //    请求网址: http://localhost:8080/shoppingCart/list
//    请求方法: GET
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId()).gt(ShoppingCart::getNumber,0);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    //添加购物车
    //请求网址: http://localhost:8080/shoppingCart/add
    //请求方法: POST
    //
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据:{}", shoppingCart);
        //先设置用户id,指定当前是哪个用户的购物车数据  因为前端没有传这个id给我们,但是这个id又非常重要（数据库这个字段不能为null）,
        //    // 所以要想办法获取到,我们在用户登录的时候就已经保存了用户的id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        //添加购物车的菜品
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //   //查询当前菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if (cartServiceOne != null) {
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            //如果不存在，则添加到购物车，数量默认就是一
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);

    }

    //    请求网址: http://localhost:8080/shoppingCart/sub
//    请求方法: POST
    @PostMapping("/sub")
    public R<String> snb(@RequestBody ShoppingCart shoppingCart) {
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (shoppingCart.getDishId() != null) {
            queryWrapper.eq(ShoppingCart::getUserId, currentId).eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            ShoppingCart serviceOne = shoppingCartService.getOne(queryWrapper);
            if (serviceOne.getNumber() == 0) {
                return R.error("减购失败");
            } else{
            Integer number = serviceOne.getNumber() - 1;
            LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper();
            updateWrapper.set(ShoppingCart::getNumber, number).eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            shoppingCartService.update(updateWrapper);
                return R.success("购减成功");
            }

        }
        if (shoppingCart.getSetmealId() != null) {
            queryWrapper.eq(ShoppingCart::getUserId, currentId).eq(ShoppingCart::getDishId, shoppingCart.getSetmealId());
            ShoppingCart serviceOne = shoppingCartService.getOne(queryWrapper);
            if (serviceOne.getNumber() == 0) {
                return R.error("减购失败");
            } else {
                Integer number = serviceOne.getNumber() - 1;
                LambdaUpdateWrapper<ShoppingCart> updateWrapper = new LambdaUpdateWrapper();
                updateWrapper.set(ShoppingCart::getNumber, number).eq(ShoppingCart::getDishId, shoppingCart.getSetmealId());
                shoppingCartService.update(updateWrapper);
                return R.success("购减成功");
            }
        }
            return R.error("失败！");

    }
    //请求网址: http://localhost:8080/shoppingCart/clean
    //请求方法: DELETE
    @DeleteMapping("/clean")
    public R<String> clean(){
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功！");
    }





}
