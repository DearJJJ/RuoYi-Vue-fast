package com.ruoyi.project.biz.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.redis.RedisCache;
import com.ruoyi.project.biz.service.WeChatOfficialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WeChatOfficialServiceImpl implements WeChatOfficialService {
    @Autowired
    private RedisCache redisCache;

    private String realGetAccessToken() {
        String appId = "wx4814875b447f3d78";
        String appSecret = "42ec5c988adf9fe0bdce1355f938e5f4";
        String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, appSecret);

        // 使用Hutool的HttpUtil进行GET请求
        String response = HttpUtil.get(url);

        if (JSONUtil.isJson(response)) {
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            String accessToken1 = jsonResponse.getStr("access_token");
            log.info("token is ---> {}", accessToken1);
            return accessToken1;
            // 打印日志
        } else {
            log.error("Invalid response received: {}", response);
            return null;
        }
    }

    @Override
    public String getAccessToken() {
        String cacheObject = redisCache.getCacheObject("Wx:Access_Token");
        if (StringUtils.isBlank(cacheObject)) {
            String accessToken = realGetAccessToken();
            redisCache.setCacheObject("Wx:Access_Token", accessToken, 2, TimeUnit.HOURS);
            cacheObject = accessToken;
        }
        return cacheObject;
    }
}
