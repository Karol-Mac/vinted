package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.MyClothesService;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

@Service
public class MyClothesServiceimpl implements MyClothesService {

    ClotheRepository clotheRepository;
    ModelMapper mapper;

    UserRepository userRepository;

    public MyClothesServiceimpl(ClotheRepository clotheRepository,
                                ModelMapper mapper, UserRepository userRepository) {
        this.clotheRepository = clotheRepository;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }


    @Override
    public ClotheDto createClothe(ClotheDto clotheDto) {

        //getting logged-in user
        User user = getUser();

        Clothe clothe = mapToEntity(clotheDto);
        clothe.setUser(user);

        //saving new clothe in DB
        Clothe savedClothe = clotheRepository.save(clothe);

        return mapToDto(savedClothe);
    }

    @Override
    public ClotheDto getClotheById(long id) {
        //getting logged-in user
        User user = getUser();

        //getting all clothes related to logged-in user
        List<Clothe> clothes = clotheRepository.findByUserId(user.getId());

        //filtering the "chosen one", by given ID
        Clothe clothe = clothes.stream().filter(clothing -> clothing.getId()==id).findAny()
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                                "You are not the owner of this clothe")
                );

        return mapToDto(clothe);
    }

    @Override
    public ClotheResponse getClothes(int pageNo, int pageSize, String sortBy,
                                        String direction) {
        //getting logged-in user
        User user = getUser();

        //define direction of sorting, and by what to sort by
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable page = PageRequest.of(pageNo, pageSize, sort);

        //getting page of clothes owned by logged-in user
        Page<Clothe> clothes = clotheRepository.findByUserId(user.getId(), page);

        List<ClotheDto> clothesDto = clothes.stream().map(this::mapToDto).toList();

        //creating ClotheResponse - bring more information about clothes to client
        ClotheResponse clotheResponse = new ClotheResponse();

        clotheResponse.setClothes(clothesDto);
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setTotalPages(clothes.getTotalPages());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(clothes.isLast());

        return clotheResponse;

    }

    @Override
    public ClotheDto updateClothe(long id, ClotheDto clotheDto) {
        Clothe clothe = getClotheFromDB(id);

        if(!clothe.getUser().equals(getUser()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "You are not the owner of this clothe");

        clothe.setName(clotheDto.getName());

        clothe.setDescription(Objects.isNull(clotheDto.getDescription()) ?
                clothe.getDescription() : clotheDto.getDescription());

        clothe.setPrice(clotheDto.getPrice());
        clothe.setSize(clotheDto.getSize());
        clothe.setImages(clotheDto.getImages());

        return mapToDto(clotheRepository.save(clothe));
    }

    @Override
    public String deleteClothe(long id) {
        Clothe clothe = getClotheFromDB(id);

        if(!clothe.getUser().equals(getUser()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "You are not the owner of this clothe");

        clotheRepository.delete(clothe);

        return "Clothe deleted successfully!";
    }

    private Clothe getClotheFromDB(long id){
        return clotheRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Clothe", "id", id)
        );
    }

    private User getUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username or email: "+ email)
        );
    }

    private Clothe mapToEntity(ClotheDto clotheDto){
        return mapper.map(clotheDto, Clothe.class);
    }

    private ClotheDto mapToDto(Clothe clothe){
        return mapper.map(clothe, ClotheDto.class);
    }

}
