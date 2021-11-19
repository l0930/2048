package com.example.demo.Control;

import com.example.demo.Mapper.UserDao;
import com.example.demo.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserDao userDao;

    @RequestMapping("insert")
    @ResponseBody
    int insert(String account, int score) {
        System.out.println(account + score);
        User user = new User(account, score);
        return userDao.insertScore(user);
    }

    @RequestMapping("select")
    @ResponseBody
    List<User> selectAll() {
        return userDao.selectAll();
    }
}
