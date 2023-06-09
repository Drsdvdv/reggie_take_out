package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("emp",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
    //添加新员工
    @PostMapping("/save")
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        String password=DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获取当前用户名ID
        Long empID= (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empID); //创建人的id,就是当前用户的id（在进行添加操作的id
        employee.setUpdateUser(empID);
        employeeService.save(employee);
        return R.success("新增员工成功！！");
    }

//    * @param page  当前页数
//     * @param pageSize 当前页最多存放数据条数,就是这一页查几条数据
//     * @param name 根据name查询员工的信息

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //测试有没有数据
       // log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);
        //钩造分页器
        Page pageInfo = new Page(page,pageSize);
        //创建LambdaQueryWrapper 构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //判断前端发送过来的名字是否为空，在进行查寻
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加一个排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //进行查寻
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);

    }
//    @GetMapping("/upada/{id}")
//    public R<String> upade(@PathVariable Long id){
//        System.out.println(id);
//        return null;
//
//    }
    @PutMapping("")
    public R<String> forbidden(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }
    @GetMapping("/{id}")
    public R<Employee> upadat(@PathVariable long id){
        Employee byId = employeeService.getById(id);
        if (byId!=null){
            return R.success(byId);
        }
        return R.error("没有此用户");
    }
}
