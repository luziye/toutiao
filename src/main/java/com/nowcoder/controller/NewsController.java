package com.nowcoder.controller;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by nowcoder on 2016/6/26.
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsDAO newsDAO;
    @Autowired
    NewsService newsService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;


    @RequestMapping(path = {"/image/"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName, HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(ToutiaoUtil.IMAGE_DIR + imageName), response.getOutputStream());

        } catch (Exception e) {
            logger.error("读取图片失败" + e.getMessage());
        }

    }

    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = newsService.saveImage(file);
            if (fileUrl == null) {
                return ToutiaoUtil.getJSONString(1, "上传失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("link") String link,
                          @RequestParam("title") String title) {
        try {
            News news = new News();
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setCommentCount(3);
            news.setLikeCount(3);
            news.setLink(link);
            news.setTitle(title);
            newsDAO.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加咨询错误" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "添加咨询失败");
        }
    }

    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String detail(@PathVariable("newsId") int newsId, Model model) {
        News news = newsService.getById(newsId);
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        if (news != null) {
            if (localUserId != 0) {
                model.addAttribute("likeStatus", likeService.getLikeStatus(localUserId, news.getId(), EntityType.ENTITY_NEWS));
            } else {
                model.addAttribute("likeStatus", 0);
            }
            //评论
            List<Comment> comments = commentService.selectByEntity(newsId, EntityType.ENTITY_NEWS);
            List<ViewObject> vos = new ArrayList<>();
            ViewObject vo = new ViewObject();
            for (Comment comment : comments) {
                vo.set("comment", comment);
                vo.set("user", userService.getUser(comment.getUserId()));
                vos.add(vo);
            }
            model.addAttribute("comments", vos);

        }
        model.addAttribute("news", news);
        model.addAttribute("owner", userService.getUser(news.getUserId()));
        return "detail";
    }

    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setStatus(0);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setEntityId(newsId);
            comment.setCreatedDate(new Date());
            comment.setContent(content);
            commentService.addComment(comment);

            //
            int count = commentService.getCommentCount(comment.getEntityId(), EntityType.ENTITY_NEWS);
            newsService.updateCommentNews(comment.getEntityId(), count);
            return "redirect:/news/" + String.valueOf(newsId);
        } catch (Exception e) {
            logger.error(e.getMessage() + "添加评论异常");
            return ToutiaoUtil.getJSONString(1, "添加失败");
        }

    }
}
