package com.example.demo.Mapper;

import com.example.demo.Model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserDao {
    @Select("select account,score from user order by score desc")
    List<User> selectAll();

    @Insert("insert into user(account,score) values(#{account},#{score})")
    int insertScore(User user);
}
