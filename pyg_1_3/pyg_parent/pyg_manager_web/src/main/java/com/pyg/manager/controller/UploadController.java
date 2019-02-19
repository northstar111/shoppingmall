package com.pyg.manager.controller;

import com.pyg.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 作者：杨立波  时间：2018/9/4 15:11
 * 上传文件
 */
@RestController
public class UploadController {

    // 注入文件服务器地址
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("uploadFile")
    public Result uploadFile(MultipartFile file) {
        // 获取扩展名
        String extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        // 使用工具类上传
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String file_id = client.uploadFile(file.getBytes(), extName, null);
            // 上传成功，返回图片路径
            return new Result(true, FILE_SERVER_URL + file_id);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败");
        }
    }
}
