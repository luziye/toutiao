package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空！！！！！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码名不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msgname", "用户名已被注册");
            return map;
        }
        user = new User();
        user.setName(username);
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        String ticket = addLoginTicker(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码名不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        //验证用户名是否存在
        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }
        //验证密码
        if (!user.getPassword().equals(ToutiaoUtil.MD5(password + user.getSalt()))) {
            map.put("msgpwd", "密码错误");
            return map;
        }

        String ticket = addLoginTicker(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    private String addLoginTicker(int userId) {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        loginTicket.setExpired(date);
        loginTicket.setStatus(0);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

}
