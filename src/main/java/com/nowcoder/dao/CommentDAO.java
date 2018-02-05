package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content, user_id, entity_id, entity_type, created_date, status ";
    String SELECT_FIELDS = " id, content, user_id, entity_id, entity_type, created_date, status";

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{content},#{userId},#{entityId},#{entityType},#{createdDate},#{status})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType}"})
    List<Comment> selectByEntity(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Select({"select count(*) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

}
