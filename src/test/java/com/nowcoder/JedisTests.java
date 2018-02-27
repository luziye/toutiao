package com.nowcoder;

import com.nowcoder.dao.*;
import com.nowcoder.model.*;
import com.nowcoder.util.JedisAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class JedisTests {
    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void jedistests() {
        User user1=new User();
        user1.setPassword("pwd");
        user1.setSalt("salt");
        user1.setName("xxyyzz");
        user1.setHeadUrl("http://www.baidu.com");

        jedisAdapter.setObject("user1xxx",user1);

        User user=jedisAdapter.getObject("user1xxx",User.class);

    }

}
