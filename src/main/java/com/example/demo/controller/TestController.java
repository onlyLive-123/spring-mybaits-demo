package com.example.demo.controller;

import com.example.demo.mapper.TestMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    @Resource
    TestMapper testMapper;


    @GetMapping("/selectList")
    public Object selectList(@RequestParam Integer status) {
        List<Map<String, Object>> mapList = testMapper.selectList(status);
        return mapList;
    }


    @GetMapping("/select")
    public Object select(@RequestParam Long id) {
        Map<String, Object> map = testMapper.selectById(id);
        return map;
    }


    @GetMapping("/insert")
    public Object insert() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", new Date().getTime());
        map.put("age", 22);
        map.put("status", 1);
        int i = testMapper.insert(map);
        return i;
    }


    @GetMapping("/update")
    public Object update(@RequestParam Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", "update_" + new Date().getTime());
        int i = testMapper.update(map);
        return i;
    }
}
