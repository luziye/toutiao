package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.dao.MessageDao;
import com.nowcoder.model.Comment;
import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by nowcoder on 2016/6/26.
 */
@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;

    public int addMessage(Message message) {
        return messageDao.addMessage(message);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit) {
        return messageDao.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDao.getConversationList(userId, offset, limit);
    }
    public int getConversationUnReadCount(int userId,String conversationId){
        return messageDao.getConversationUnReadCount(userId,conversationId);
    }


}
