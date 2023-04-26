package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Orders;

public interface OrdersService extends IService< Orders> {
    void submit(Orders orders);
}
