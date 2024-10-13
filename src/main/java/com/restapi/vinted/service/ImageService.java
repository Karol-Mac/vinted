package com.restapi.vinted.service;

import com.restapi.vinted.entity.Clothe;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    String saveImage(MultipartFile file);

    Resource getImage(String imageName) throws IOException;

    void updateImages(Clothe clothe, List<MultipartFile> newImages, List<String> deletedImages);

    void deleteImage(String imageName);
}
