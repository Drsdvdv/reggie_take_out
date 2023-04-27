package com.itheima.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.OrderDetailService;
import com.itheima.reggie.service.OrdersService;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;
    //请求网址: http://localhost:8080/order/page?page=1&pageSize=10&number=%2B165&beginTime=2023-04-04%2000%3A00%3A00&endTime=2023-05-29%2023%3A59%3A59
    //请求方法: GET
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String number,String beginTime,String endTime){
        Page<Orders> OrdersPage = new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper();
        wrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        wrapper.in(StringUtils.isNotEmpty(beginTime),Orders::getOrderTime,beginTime);
        wrapper.lt(StringUtils.isNotEmpty(endTime),Orders::getOrderTime,endTime);
        wrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(OrdersPage,wrapper);
        return R.success(OrdersPage);
    }
    //请求 URL: http://localhost:8080/order/userPage?page=1&pageSize=1
    //请求方法: GET
    @GetMapping("/userPage")
    public R<Page> getUserpage(@RequestParam("page") int page,@RequestParam("pageSize") int pageSize){
        Page<Orders> page1 = new Page<>(page,pageSize);
        ordersService.page(page1);
        return R.success(page1);
    }
    //请求 URL: http://localhost:8080/order/submit
    //请求方法: POST
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }
    @PutMapping
    public R<String> put(@RequestBody Orders orders){
        ordersService.updateById(orders);
        return R.success("修改成功");
    }
    //在来一单
    //请求 URL: http://localhost:8080/order/again
    //请求方法: POST
    @PostMapping("/again")
    public R<String> again(@RequestBody Map<String,String> map){
        String ids=map.get("id");
        long id = Long.parseLong(ids);
        LambdaQueryWrapper<OrderDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDetail::getOrderId,id);
        //获取订单详情表
        List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapper);
        //通过用户id把原来的购物车清空
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper1);
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map((item) -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(currentId);
            shoppingCart.setImage(item.getImage());
            Long dishId = item.getDishId();
            Long setmealId = item.getSetmealId();
            if (dishId != null) {
                //如果是菜品那就添加菜品的查询条件
                shoppingCart.setDishId(dishId);
            } else {
                //添加到购物车的是套餐
                shoppingCart.setSetmealId(setmealId);
            }
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        shoppingCartService.saveBatch(shoppingCartList);
        return R.success("操作成功");
    }
}
