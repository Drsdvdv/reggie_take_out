package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private AddressBookService addressBookService;
    //请求网址: http://localhost:8080/addressBook/default
    //请求方法: GET
    //默认地址
    @PutMapping("default")
    public R<AddressBook> Default(@RequestBody AddressBook addressBook){
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return R.success(addressBook);

    }
    //主要对于前端
    @GetMapping("/default")
    public R<AddressBook> getdefault(){
        String one1 = redisTemplate.opsForValue().get("one");
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId()).eq(AddressBook::getIsDefault,1);
        AddressBook one = addressBookService.getOne(queryWrapper);
        if (null == one) {
            return R.error("没有找到该对象");
        } else {
            return R.success(one);
        }
    }
    //请求网址: http://localhost:8080/addressBook/list
    //请求方法: GET
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }
    //请求网址: http://localhost:8080/addressBook/1417414526093082626
    //请求方法: GET
    @GetMapping("/{id}")
    public R<AddressBook> getID(@PathVariable Long id){
        AddressBook byId = addressBookService.getById(id);
        return R.success(byId);
    }
    //请求 URL: http://localhost:8080/addressBook
    //请求方法: PUT
    @PutMapping
    public R<String> Put(@RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook);
        return R.success("修改成功");
    }
    @PostMapping
    public R<String> add(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setCreateTime(LocalDateTime.now());
        addressBook.setUpdateTime(LocalDateTime.now());
        addressBook.setCreateUser(BaseContext.getCurrentId());
        addressBook.setUpdateUser(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("添加成功");
    }
}
