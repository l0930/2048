package com.example.demo;

import com.example.demo.Mapper.UserDao;
import com.example.demo.Model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
    }

    @Test
    void test() {
        userDao.selectAll().forEach(System.out::println);
    }

    @Test
    void test1() {
        User user=new User("111",500);
        int i = userDao.insertScore(user);
        System.out.println(i);
    }
}
