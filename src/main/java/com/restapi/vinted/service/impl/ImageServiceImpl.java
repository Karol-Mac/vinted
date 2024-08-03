package com.restapi.vinted.service.impl;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${image.upload.dir}")
    private String imageDirectory;

    @Override
    public String saveImage(MultipartFile file) {
        if(file.isEmpty()) throw new ApiException(HttpStatus.BAD_REQUEST, "Image file must not be empty");

        String imageName = UUID.randomUUID()+ "_" + file.getOriginalFilename();
        Path directoryPath = Paths.get(imageDirectory);

        if (!Files.exists(directoryPath))
            try {
                Files.createDirectories(directoryPath);

                Path filePath = directoryPath.resolve(imageName);
                Files.write(filePath, file.getBytes());
                return imageName;

            } catch (IOException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        return null;
    }

    @Override
    public Resource getImage(String imageName) throws IOException {
        Path filePath = Paths.get(imageDirectory).resolve(imageName);
        if (Files.exists(filePath)) {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("Could not read the file: " + imageName);
            }
        } else {
            throw new IOException("File not found: " + imageName);
        }
    }

    @Override
    public boolean deleteImage(String imageName) throws IOException{
        return false;
    }
}
