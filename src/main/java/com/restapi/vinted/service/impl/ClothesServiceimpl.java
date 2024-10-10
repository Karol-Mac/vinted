package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.CategoryRepository;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.ImageService;
import com.restapi.vinted.service.ClothesService;
import com.restapi.vinted.utils.Constant;
import org.modelmapper.ModelMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ClothesServiceimpl implements ClothesService {

    private static final Logger log = LoggerFactory.getLogger(ClothesServiceimpl.class);
    private final ClotheRepository clotheRepository;
    private final ImageService imageService;
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;


    public ClothesServiceimpl(ClotheRepository clotheRepository, ImageService imageService,
                              ModelMapper mapper, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.clotheRepository = clotheRepository;
        this.imageService = imageService;
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
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

        return getClotheResponse(pageNo, pageSize, clothes);
    }

    @Override
    @Transactional
    public ClotheDto getClotheById(long clotheId, String email) {

        log.warn("Principal's name: {}", email);

        Clothe clothe = clotheRepository.findById(clotheId)
                .orElseThrow( ()-> new ResourceNotFoundException("Clothe", "id", clotheId));

        //increment view's count if any user (expect the owner) looks at clothing
        if(!clothe.getUser().equals(getUser(email))){
            clothe.setViews(clothe.getViews() + 1);
            clotheRepository.save(clothe);
        }

        return mapToDto(clothe);
    }
    
    // only logged-in user action
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

        return getClotheResponse(pageNo, pageSize, clothes);
    }

    // owner action
    @Override
    @PreAuthorize("@clothesServiceimpl.isOwner(#id, #email)")
    @Transactional
    public ClotheDto updateClothe(long id, ClotheDto clotheDto,
                                  List<MultipartFile> newImages, List<String> deletedImages, String email) {

        Clothe clothe = getClotheFromDB(id);

        clothe.setName(clotheDto.getName());
        clothe.setDescription(clotheDto.getDescription());
        clothe.setPrice(clotheDto.getPrice());
        clothe.setSize(clotheDto.getSize());

        updateImages(clothe, newImages, deletedImages);

        Clothe updatedClothe = clotheRepository.save(clothe);
        return mapToDto(updatedClothe);
    }

    // owner action
    @Override
    @PreAuthorize("@clothesServiceimpl.isOwner(#id, #email)")
    @Transactional
    public String deleteClothe(long id, String email) {

        Clothe clothe = getClotheFromDB(id);
        clothe.getImages().forEach(imageService::deleteImage);

        clotheRepository.delete(clothe);
        return "Clothe deleted successfully!";
    }

    @Transactional(readOnly = true)
    public boolean isOwner(long clotheId, String email){
        var clothe = getClotheFromDB(clotheId);
        var user = getUser(email);
        if (!clothe.getUser().equals(user))
            throw new ApiException(HttpStatus.FORBIDDEN, Constant.NOT_OWNER);
        return true;
    }

    private void updateImages(Clothe clothe, List<MultipartFile> newImages, List<String> deletedImages) {
        if (deletedImages != null && !deletedImages.isEmpty()) {
            clothe.getImages().removeAll(deletedImages);
            deletedImages.forEach(imageService::deleteImage);
        }

        if(clothe.getImages().size() + newImages.size() > 5)
            throw new ApiException(HttpStatus.BAD_REQUEST, Constant.IMAGES_VALIDATION_FAILED);

        if (newImages != null && !newImages.isEmpty()) {
            var newImageNames = newImages.stream().map(imageService::saveImage).toList();
            clothe.getImages().addAll(newImageNames);
        }
    }

    private Clothe getClotheFromDB(long id){
        return clotheRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Clothe", "id", id)
        );
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

    private ClotheResponse getClotheResponse(int pageNo, int pageSize, Page<Clothe> clothes){
        ClotheResponse clotheResponse = new ClotheResponse();

        clotheResponse.setClothes(clothes.stream().map(this::mapToDto).toList());
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setTotalPages(clothes.getTotalPages());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(clothes.isLast());
        return clotheResponse;
    }
}