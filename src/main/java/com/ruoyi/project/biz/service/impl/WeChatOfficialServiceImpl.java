package com.ruoyi.project.biz.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.project.biz.service.WeChatOfficialService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeChatOfficialServiceImpl implements WeChatOfficialService {
    private String accessToken = "";

    private void realGetAccessToken() {
        String appId = "xxxxx";
        String appSecret = "xxxxx";
        String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId, appSecret);

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        if (response != null) {
            this.accessToken = response.get("access_token").asText();
        }
    }

    @Override
    public String getAccessToken() {
        return this.accessToken;
    }

    @Scheduled(fixedRate = 7140000)
    public void run() {
        realGetAccessToken();
    }
}
