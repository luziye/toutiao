package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.html.ObjectView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nowcoder on 2016/6/26.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String getConversationList(Model model) {
        try{
            int localUser=hostHolder.getUser().getId();
            List<ViewObject> conversations=new ArrayList<>();
            List<Message> messageList=messageService.getConversationList(localUser,0,10);
            for (Message message:messageList){
                ViewObject vo=new ViewObject();
                vo.set("conversation",message);
                int targetId=message.getFromId()==localUser?message.getToId():message.getFromId();
                User targetUser=userService.getUser(targetId);
                vo.set("user",targetUser);
                vo.set("hasRead",messageService.getConversationUnReadCount(localUser,message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);

        }catch (Exception e){
            logger.error(e.getMessage()+"获取咨询列表异常");

        }
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try {
            List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> vos = new ArrayList<>();
            for (Message msg : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userName", user.getName());
                vos.add(vo);
            }
            model.addAttribute("messages", vos);
            return "letterDetail";
        } catch (Exception e) {
            logger.error(e.getMessage() + "获取消息异常");
        }
        return "letterDetail";
    }


    @RequestMapping("/msg/addMessage")
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(fromId);
            message.setToId(toId);
            message.setHasRead(0);
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) :
                    String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);
            return ToutiaoUtil.getJSONString(message.getId());
        } catch (Exception e) {
            logger.error(e.getMessage() + "添加消息异常");
            return ToutiaoUtil.getJSONString(1, "添加消息失败");
        }

    }
}
