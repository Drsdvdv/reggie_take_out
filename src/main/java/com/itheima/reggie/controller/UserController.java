package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //请求网址: http://localhost:8080/user/login
    //请求方法: POST
    @PostMapping("/login")
    public R<User> login(@RequestBody User user1, HttpSession session){
        //System.out.println(user);
        String phone = user1.getPhone();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.eq(User::getPhone,phone);
        User user = userService.getOne(wrapper);
        if (user==null){
            //没有此用户 自动注册
            user=new User();
            user.setPhone(user.getPhone());
            user.setStatus(1);
            userService.save(user);
        }
        //登录成功！
        session.setAttribute("user",user.getId());
        return R.success(user);
    }
    //请求 URL: http://localhost:8080/user/loginout
    //请求方法: POST
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}
