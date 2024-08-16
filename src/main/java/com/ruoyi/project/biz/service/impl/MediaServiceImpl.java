package com.ruoyi.project.biz.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class MediaServiceImpl {
    public String upload(String accessToken, MultipartFile file, String mediaType) throws IOException {
        // 构造请求URL
        String postUrl = String.format("https://api.weixin.qq.com/cgi-bin/media/upload?access_token=%s&type=%s", accessToken, mediaType);

        // 将 MultipartFile 转换为 File
        File tempFile = File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile);  // 将文件写入到临时文件

        // 使用Hutool的HttpRequest模拟POST/FORM请求上传文件
        HttpResponse response = HttpRequest.post(postUrl)
                .form("media", tempFile)  // 上传文件
                .form("type", mediaType)  // 上传文件
                .execute();

        // 删除临时文件
        tempFile.delete();

        // 返回响应内容
        return response.body();
    }

    public void getMedia(String accessToken, String mediaId) throws IOException {
        String postUrl = String.format("https://api.weixin.qq.com/cgi-bin/media/get?access_token=%s&media_id=%s", accessToken, mediaId);

        // 使用 Hutool 发起 GET 请求
        HttpResponse response = HttpUtil.createGet(postUrl).execute();

        // 获取响应头的 Content-Type
        String contentType = response.header("Content-Type");

        // 如果是 JSON 响应，直接打印出来
        if (contentType != null && (contentType.contains("application/json") || contentType.contains("text/plain"))) {
            String jsonResponse = response.body();
            log.info("JSON Response: " + jsonResponse);
        } else {
            // 处理二进制数据，保存为文件
            byte[] mediaData = response.bodyBytes();

            // 将二进制数据保存为文件
            try (FileOutputStream outputStream = new FileOutputStream("test_media.jpg")) {
                outputStream.write(mediaData);
                log.info("File downloaded successfully as test_media.jpg");
            }
        }
    }
}
