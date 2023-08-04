package com.sms.controller;

import com.sms.common.Result;
import com.sms.entity.Notice;
import com.sms.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Api(tags = "公告相关接口")
@RestController
@RequestMapping("/notice")
@Slf4j
public class NoticeController {
    @Resource
    UserService userService;

    @Resource
    RedisTemplate redisTemplate;

    /**
     * 公告设置接口 可根据grant字段设置谁能收到公告
     *
     * @param notice
     * @return
     */
    @ApiOperation("公告添加")
    @PostMapping("/set")
    public Result<String> setNotice(@RequestBody Notice notice) {
        if (!userService.isAdmin()) {
            return Result.error("当前用户没有该操作权限");
        }
        Integer[] grants = notice.getGrants();
        for (Integer grant : grants) {
            redisTemplate.delete("notice:" + grant);
            redisTemplate.boundValueOps("notice:" + grant)
                    .set(notice, notice.getHours(), TimeUnit.HOURS);
        }
        log.info("设置了通知，标题为:{}", notice.getTitle());
        return Result.success("公告设置成功");
    }

    @ApiOperation("公告获取")
    @PostMapping("/get")
    public Result<String> getNotice() {
        Integer grantedLevel = userService.grantLevel();
        try {
            String notice = redisTemplate.opsForValue().get("notice:" + grantedLevel).toString();
            return Result.success(notice);
        } catch (NullPointerException e) {
            log.error(String.valueOf(e));
            return Result.error("该用户没有通知信息");
        }
    }
}
