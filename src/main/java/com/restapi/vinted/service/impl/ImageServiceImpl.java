package com.restapi.vinted.service.impl;

import com.restapi.vinted.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public String saveImage(MultipartFile file) throws IOException{
        return "";
    }

    @Override
    public Resource getImage(String imageName) throws IOException{
        return null;
    }

    @Override
    public boolean deleteImage(String imageName) throws IOException{
        return false;
    }
}
