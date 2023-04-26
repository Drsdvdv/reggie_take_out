package com.itheima.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
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
}
