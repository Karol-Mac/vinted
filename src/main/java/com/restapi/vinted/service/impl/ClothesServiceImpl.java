package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.ImageService;
import com.restapi.vinted.service.ClothesService;
import com.restapi.vinted.utils.ClotheUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class ClothesServiceImpl implements ClothesService {

    private static final Logger log = LoggerFactory.getLogger(ClothesServiceImpl.class);
    private final ClotheRepository clotheRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ClotheUtils clotheUtils;


    public ClothesServiceImpl(ClotheRepository clotheRepository, ImageService imageService,
                              UserRepository userRepository, CategoryRepository categoryRepository, ClotheUtils clotheUtils) {
        this.clotheRepository = clotheRepository;
        this.imageService = imageService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.clotheUtils = clotheUtils;
    }


    @Override
    @PreAuthorize("permitAll()")
    public ClotheResponse getAllClothesByCategory(long categoryId, int pageNo, int pageSize,
                                                  String sortBy, String direction){

        //create Sort, and Page object
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNo, pageSize, sort);

        //create Page<Clothe> with custom DB method
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        Page<Clothe> clothes = clotheRepository.findByCategoryId(categoryId, page);

        return clotheUtils.getClotheResponse(pageNo, pageSize, clothes);
    }

    @Override
    @Transactional
    @PreAuthorize("permitAll()")
    public ClotheDto getClotheById(long clotheId, Optional<Principal> principal) {

        Clothe clothe = clotheRepository.findById(clotheId)
                .orElseThrow( ()-> new ResourceNotFoundException("Clothe", "id", clotheId));

        //update of view's field:
        if(principal.isEmpty() || (
                    principal.isPresent() &&
                    !clothe.getUser().equals(getUser(principal.get().getName())))) {
            clothe.setViews(clothe.getViews() + 1);
            clotheRepository.save(clothe);
        }

        return clotheUtils.mapToDto(clothe);
    }
    
    // only logged-in user action
    @Override
    @Transactional
    public ClotheDto addClothe(ClotheDto clotheDto, List<MultipartFile> images, String email) {
        User user = getUser(email);

        Clothe clothe = clotheUtils.mapToEntity(clotheDto);
        clothe.setUser(user);

        log.warn("Clothe Category: {}", clothe.getCategory());

        var imageNames = images.stream().map(imageService::saveImage).toList();
        clothe.setImages(imageNames);

        Clothe savedClothe = clotheRepository.save(clothe);

        log.warn("Clothe Category: {}", savedClothe.getCategory());

        return clotheUtils.mapToDto(savedClothe);
    }

    // only logged-in user action
    @Override
    @Transactional(readOnly = true)
    public ClotheResponse getMyClothes(int pageNo, int pageSize, String sortBy,
                                       String direction, String email) {
        //getting logged-in user
        User user = getUser(email);

        //define the direction of sorting, and by what to sort by
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNo, pageSize, sort);

        //getting page of clothes owned by logged-in user
        Page<Clothe> clothes = clotheRepository.findByUserId(user.getId(), page);

        return clotheUtils.getClotheResponse(pageNo, pageSize, clothes);
    }

    // owner action
    @Override
    @PreAuthorize("@clotheUtils.isOwner(#id, #email)")
    @Transactional
    public ClotheDto updateClothe(long id, ClotheDto clotheDto,
                                  List<MultipartFile> newImages, List<String> deletedImages,String email) {

        Clothe clothe = clotheUtils.getClotheFromDB(id);

        clothe.setName(clotheDto.getName());
        clothe.setDescription(clotheDto.getDescription());
        clothe.setPrice(clotheDto.getPrice());
        clothe.setSize(clotheDto.getSize());
        clothe.setMaterial(clotheDto.getMaterial());

        //user may (theoretically) want to change the category of his clothing
        clothe.setCategory(categoryRepository.findById(clotheDto.getCategoryId())
                .orElseThrow( () -> new ResourceNotFoundException("Category", "id", clotheDto.getId())));

        imageService.updateImages(clothe, newImages, deletedImages);

        Clothe updatedClothe = clotheRepository.save(clothe);
        return clotheUtils.mapToDto(updatedClothe);
    }

    // owner action
    @Override
    @PreAuthorize("@clotheUtils.isOwner(#id, #email)")
    @Transactional
    public String deleteClothe(long id, String email) {

        Clothe clothe = clotheUtils.getClotheFromDB(id);
        clothe.getImages().forEach(imageService::deleteImage);

        clotheRepository.delete(clothe);
        return "Clothe deleted successfully!";
    }

    private User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }
}