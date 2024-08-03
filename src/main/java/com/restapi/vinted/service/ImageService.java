package com.restapi.vinted.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    String saveImage(MultipartFile file);

    Resource getImage(String imageName) throws IOException;

    boolean deleteImage(String imageName) throws IOException;
}
