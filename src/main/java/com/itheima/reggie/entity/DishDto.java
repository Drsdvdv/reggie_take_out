package com.itheima.reggie.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors = new ArrayList<>();
    private String categoryName;  //后面要用的
    private Integer copies;  //后面要用的
}
