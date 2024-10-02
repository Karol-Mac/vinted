package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.ImageService;
import com.restapi.vinted.service.MyClothesService;
import com.restapi.vinted.utils.Constant;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class MyClothesServiceimpl implements MyClothesService {

    private final ClotheRepository clotheRepository;
    private final ImageService imageService;
    private final ModelMapper mapper;
    private final UserRepository userRepository;

    public MyClothesServiceimpl(ClotheRepository clotheRepository, ImageService imageService,
                                ModelMapper mapper, UserRepository userRepository) {
        this.clotheRepository = clotheRepository;
        this.imageService = imageService;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }


    @Override
    @Transactional
    public ClotheDto addClothe(ClotheDto clotheDto, List<MultipartFile> images, String email) {
        User user = getUser(email);

        Clothe clothe = mapToEntity(clotheDto);
        clothe.setUser(user);

        var imageNames = images.stream().map(imageService::saveImage).toList();
        clothe.setImages(imageNames);

        Clothe savedClothe = clotheRepository.save(clothe);
        return mapToDto(savedClothe);
    }

    @Override
    @PreAuthorize("@myClothesServiceimpl.isOwner(#id, #email)")
    @Transactional(readOnly = true)
    public ClotheDto getClotheById(long id, String email) {
        //getting logged-in user
        User user = getUser(email);

        if(!clotheRepository.existsById(id))
            throw new ResourceNotFoundException("Clothe", "id", id);

        //getting all clothes related to logged-in user
        List<Clothe> clothes = clotheRepository.findByUserId(user.getId());

        //filtering the "chosen one", by given ID
        Clothe clothe = clothes.stream().filter(clo -> clo.getId()==id).findAny()
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, Constant.NOT_OWNER));

        return mapToDto(clothe);
    }

    @Override
    @Transactional(readOnly = true)
    public ClotheResponse getClothes(int pageNo, int pageSize, String sortBy,
                                        String direction, String email) {
        //getting logged-in user
        User user = getUser(email);

        //define the direction of sorting, and by what to sort by
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNo, pageSize, sort);

        //getting page of clothes owned by logged-in user
        Page<Clothe> clothes = clotheRepository.findByUserId(user.getId(), page);

        //creating ClotheResponse - bring more information about clothes to a client
        ClotheResponse clotheResponse = new ClotheResponse();

        clotheResponse.setClothes(clothes.stream().map(this::mapToDto).toList());
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setTotalPages(clothes.getTotalPages());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(clothes.isLast());

        return clotheResponse;
    }

    @Override
    @PreAuthorize("@myClothesServiceimpl.isOwner(#id, #email)")
    @Transactional
    public ClotheDto updateClothe(long id, ClotheDto clotheDto,
                                  List<MultipartFile> newImages, List<String> deletedImages, String email) {

        Clothe clothe = getClotheFromDB(id);

        clothe.setName(clotheDto.getName());
        clothe.setDescription(clotheDto.getDescription());
        clothe.setPrice(clotheDto.getPrice());
        clothe.setSize(clotheDto.getSize());

        if (newImages != null && !newImages.isEmpty()) {
            var newImageNames = newImages.stream().map(imageService::saveImage).toList();
            clothe.getImages().addAll(newImageNames);
        }

        if (deletedImages != null && !deletedImages.isEmpty()) {
            clothe.getImages().removeAll(deletedImages);
            deletedImages.forEach(imageService::deleteImage);
        }

        if(clothe.getImages().size() > 5)
            throw new ApiException(HttpStatus.BAD_REQUEST, Constant.IMAGES_VALIDATION_FAILED);

        Clothe updatedClothe = clotheRepository.save(clothe);
        return mapToDto(updatedClothe);
    }


    @Override
    @PreAuthorize("@myClothesServiceimpl.isOwner(#id, #email)")
    @Transactional
    public String deleteClothe(long id, String email) {

        Clothe clothe = getClotheFromDB(id);
        clothe.getImages().forEach(imageService::deleteImage);

        clotheRepository.delete(clothe);
        return "Clothe deleted successfully!";
    }



    private Clothe getClotheFromDB(long id){
        return clotheRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Clothe", "id", id)
        );
    }

    @Transactional(readOnly = true)
    public boolean isOwner(long clotheId, String email){
        var clothe = getClotheFromDB(clotheId);
        var user = getUser(email);
        if (!clothe.getUser().equals(user))
            throw new ApiException(HttpStatus.FORBIDDEN, Constant.NOT_OWNER);
        return true;
    }

    private User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(
                        () -> new ResourceNotFoundException("User", "email", email));
    }

    private Clothe mapToEntity(ClotheDto clotheDto){
        return mapper.map(clotheDto, Clothe.class);
    }

    private ClotheDto mapToDto(Clothe clothe){
        return mapper.map(clothe, ClotheDto.class);
    }
}