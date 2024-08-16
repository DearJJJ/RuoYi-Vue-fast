package com.ruoyi.project.biz.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ruoyi.project.biz.domain.TNoticeMsg;
import com.ruoyi.project.biz.pojo.ImageMessage;
import com.ruoyi.project.biz.pojo.ImageMessageReader;
import com.ruoyi.project.biz.pojo.TextMessage;
import com.ruoyi.project.biz.pojo.WeChatMessage;
import com.ruoyi.project.biz.service.TNoticeMsgService;
import com.ruoyi.project.biz.service.WeChatOfficialService;
import com.ruoyi.project.biz.service.impl.MediaServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/wx")
public class WxTestController {
    @Autowired
    private TNoticeMsgService tNoticeMsgService;
    @Autowired
    private WeChatOfficialService weChatOfficialService;
    @Autowired
    private MediaServiceImpl mediaService;

    private final String TOKEN = "isliaoqstoken"; // 请按照公众平台官网\基本配置中信息填写

    @GetMapping
    public String handleGet(
            @RequestParam(value = "signature", required = false) String signature,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "nonce", required = false) String nonce,
            @RequestParam(value = "echostr", required = false) String echostr) {
        try {
            if (signature == null || timestamp == null || nonce == null || echostr == null) {
                return "hello, this is handle view";
            }

            String[] list = {TOKEN, timestamp, nonce};
            Arrays.sort(list);

            StringBuilder content = new StringBuilder();
            for (String item : list) {
                content.append(item);
            }

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(content.toString().getBytes());

            StringBuilder hexStr = new StringBuilder();
            for (byte b : digest) {
                String shaHex = Integer.toHexString(b & 0xFF);
                if (shaHex.length() < 2) {
                    hexStr.append(0);
                }
                hexStr.append(shaHex);
            }

            String hashcode = hexStr.toString();
            log.info("handle/GET func: hashcode, signature: {}, {}", hashcode, signature);

            if (hashcode.equals(signature)) {
                return echostr;
            } else {
                return "";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    @PostMapping
    public String handleMsgPost(@RequestBody String webData) {
        try {
            log.info("Handle Post webData is: {}", webData);
            XmlMapper xmlMapper = new XmlMapper();
            WeChatMessage recMsg = xmlMapper.readValue(new StringReader(webData), WeChatMessage.class);
            String toUser = recMsg.getFromUserName();
            String fromUser = recMsg.getToUserName();

            String responseMessage = "success";  // Default response

            switch (recMsg.getMsgType()) {
                case "text":
                    responseMessage = handleTextMessage(webData, xmlMapper, toUser, fromUser);
                    break;

                case "image":
                    responseMessage = handleImageMessage(webData, xmlMapper, toUser, fromUser);
                    break;

                default:
                    log.info("暂且不处理");
                    break;
            }

            log.info("return message: {}", responseMessage);
            return responseMessage;
        } catch (Exception e) {
            log.error("Error handling message", e);
            return e.getMessage();
        }
    }

    private String handleTextMessage(String webData, XmlMapper xmlMapper, String toUser, String fromUser) throws IOException {
        TextMessage recMsg2 = xmlMapper.readValue(new StringReader(webData), TextMessage.class);
        String content2 = recMsg2.getContent();
        StringBuilder builder = new StringBuilder();

        if ("过期时间".equals(content2)) {
            List<TNoticeMsg> list = tNoticeMsgService.list();
            for (TNoticeMsg tNoticeMsg : list) {
                builder.append(formatNoticeMsg(tNoticeMsg)).append("--------------\n");
            }
        } else {
            builder.append("test");
        }

        TextMessage textMessage = new TextMessage(toUser, fromUser, builder.toString());
        return xmlMapper.writeValueAsString(textMessage);
    }

    private String handleImageMessage(String webData, XmlMapper xmlMapper, String toUser, String fromUser) throws IOException {
        ImageMessageReader recMsg2 = xmlMapper.readValue(new StringReader(webData), ImageMessageReader.class);
        ImageMessage imageMessage = new ImageMessage(toUser, fromUser, recMsg2.getMediaId());
        return xmlMapper.writeValueAsString(imageMessage);
    }

    private String formatNoticeMsg(TNoticeMsg tNoticeMsg) {
        Date invalidTime = tNoticeMsg.getInvalidTime();
        return new StringBuilder()
                .append("标题：").append(tNoticeMsg.getType()).append("\n")
                .append("内容：").append(tNoticeMsg.getContent()).append("\n")
                .append("过期时间：").append(DateUtil.format(invalidTime, "yyyy/MM/dd")).append("\n")
                .append("剩余天数：").append(DateUtil.between(new Date(), invalidTime, DateUnit.DAY)).append("\n")
                .toString();
    }

    @PostMapping("/upload")
    public String uploadMedia(@RequestParam("file") MultipartFile file,
                              @RequestParam("mediaType") String mediaType) throws IOException {
        String accessToken = weChatOfficialService.getAccessToken();
        return mediaService.upload(accessToken, file, mediaType);
    }

    @PostMapping("/getMedia")
    public String getMedia(@RequestParam("accessToken") String accessToken,
                           @RequestParam("mediaId") String mediaId) throws IOException {
        mediaService.getMedia(accessToken, mediaId);
        return "Media retrieval initiated!";
    }
}
