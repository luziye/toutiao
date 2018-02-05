package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by nowcoder on 2016/6/26.
 */
@Controller
public class LikeController {

    private static final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    NewsService newsService;

    @RequestMapping(value = "/like", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("newsId") int newsId) {
        long likeCount = 0;
        if (hostHolder.getUser() != null) {
            likeCount = likeService.like(hostHolder.getUser().getId(), newsId, EntityType.ENTITY_NEWS);
        }
        newsService.updateLikeCount(newsId, (int) likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(value = "/dislike", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String dislike(@RequestParam("newsId") int newsId) {
        long dislikeCount = 0;
        if (hostHolder.getUser() != null) {
            dislikeCount = likeService.dislike(hostHolder.getUser().getId(), newsId, EntityType.ENTITY_NEWS);
        }
        newsService.updateLikeCount(newsId, (int) dislikeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(dislikeCount));
    }
}
