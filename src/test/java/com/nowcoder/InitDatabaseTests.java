package com.nowcoder;

import com.nowcoder.dao.*;
import com.nowcoder.model.*;
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
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;
    @Autowired
    CommentDAO commentDAO;
    @Autowired
    NewsDAO newsDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    MessageDao messageDao;



    @Test
    public void initData() {
        Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            News news = new News();
            news.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
            news.setLikeCount(i + 1);
            news.setUserId(i + 1);
            news.setTitle(String.format("TITLE{%d}", i));
            news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
            newsDAO.addNews(news);

            for (int j = 0; j < 3; j++) {
                Comment comment = new Comment();
                comment.setContent("this is a big mistake" + String.valueOf(j));
                comment.setCreatedDate(new Date());
                comment.setEntityId(news.getId());
                comment.setEntityType(EntityType.ENTITY_NEWS);
                comment.setStatus(0);
                comment.setUserId(i + 1);
                commentDAO.addComment(comment);
            }
            Message message=new Message();
            message.setFromId(5);
            message.setToId(10);
            message.setCreatedDate(new Date());
            message.setContent("hahahha"+i);
            message.setHasRead(0);
            message.setConversationId("5-10");
            messageDao.addMessage(message);

            user.setPassword("newpassword");
            userDAO.updatePassword(user);

            LoginTicket loginTicket = new LoginTicket();
            loginTicket.setUserId(i + 1);
            loginTicket.setExpired(date);
            loginTicket.setStatus(0);
            loginTicket.setTicket(String.format("TICKET%d", i + 1));
            loginTicketDAO.addTicket(loginTicket);
            loginTicketDAO.updateStatus(loginTicket.getTicket(), 1);
        }

        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
//        userDAO.deleteById(1);
//        Assert.assertNull(userDAO.selectById(1));
        Assert.assertEquals(8, loginTicketDAO.selectByTicket("TICKET8").getUserId());

    }

}
