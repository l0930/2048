package com.example.a2048;

import com.example.a2048.Model.User;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void text(){
        User userNo = new User("123", 300);
        String sql = "select * from record where name='" + userNo.getAccount() + "'" + "and score >" + userNo.getScore();
        System.out.println(sql);
    }
}
