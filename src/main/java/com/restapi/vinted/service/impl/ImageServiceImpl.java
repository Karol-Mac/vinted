package com.restapi.vinted.service.impl;
import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.ImageService;
import com.restapi.vinted.utils.Constant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;

    public ImageServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public String saveImage(MultipartFile file){
        if(file.isEmpty()) throw new ApiException( HttpStatus.BAD_REQUEST,"Image file must not be empty");

        String imageName = UUID.randomUUID()+ "_" + file.getOriginalFilename();
        Path directoryPath = Paths.get(imageDirectory);

        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        Path filePath = directoryPath.resolve(imageName);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            throw new ApiException(HttpStatus.CONFLICT , e.getMessage());
        }

        return imageName;
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
    public void deleteImage(String imageName) {
        Path filePath = Paths.get(imageDirectory).resolve(imageName);

        if (Files.exists(filePath)) {
            try {
                Files.delete(filePath);
            } catch (IOException e) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "Could not delete the file: " + imageName);
            }
        } else {
            throw new ResourceNotFoundException("File", imageName);
        }
    }

//    private void isOwner(Clothe clothe){
//        if(!clothe.getUser().equals(getUser()))
//            throw new ApiException(HttpStatus.FORBIDDEN, Constant.NOT_OWNER);
//    }
//
//
//    private User getUser(){
//        String usernameOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
//                () -> new ResourceNotFoundException("User", "username or email", usernameOrEmail)
//        );
//    }
}
