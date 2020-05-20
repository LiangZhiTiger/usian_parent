package com.usian.comtroller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.usian.config.FastClientImporter;
import com.usian.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileUploadController {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpg","image/gif","image/jpeg","image/png");

    /**
     * 上传图片
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping("/upload")
    public Result fileUpload(MultipartFile file) throws IOException {
        //判断文件类型
        if(!CONTENT_TYPES.contains(file.getContentType())){
            return Result.error("文件格式不对");
        }
        //判断文件是否为空
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if(bufferedImage==null){
            return Result.error("文件为空");
        }
        //保存到服务器
        String originalFilename = file.getOriginalFilename();
        StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), StringUtils.substringAfterLast(originalFilename, "."), null);
        System.out.println(storePath.getFullPath());

        //生成url返回
        return Result.ok("http://image.usian.com/"+storePath.getFullPath());
    }




}
