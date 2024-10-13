package com.restapi.vinted.utils;

import com.restapi.vinted.entity.Clothe;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ClotheDto;
import com.restapi.vinted.payload.ClotheResponse;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ClotheUtils {

    private final ClotheRepository clotheRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public ClotheUtils(ClotheRepository clotheRepository, UserRepository userRepository, ModelMapper mapper) {
        this.clotheRepository = clotheRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }


    @Transactional(readOnly = true)
    public boolean isOwner(long clotheId, String email){
        var clothe = getClotheFromDB(clotheId);
        var user = getUser(email);
        if (!clothe.getUser().equals(user))
            throw new ApiException(HttpStatus.FORBIDDEN, Constant.NOT_OWNER);
        return true;
    }

    public Clothe getClotheFromDB(long id){
        return clotheRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Clothe", "id", id)
        );
    }

    public User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }

    public ClotheResponse getClotheResponse(int pageNo, int pageSize, Page<Clothe> clothes){
        ClotheResponse clotheResponse = new ClotheResponse();

        clotheResponse.setClothes(clothes.stream().map(this::mapToDto).toList());
        clotheResponse.setPageNo(pageNo);
        clotheResponse.setTotalPages(clothes.getTotalPages());
        clotheResponse.setPageSize(pageSize);
        clotheResponse.setLast(clothes.isLast());
        return clotheResponse;
    }

    public Clothe mapToEntity(ClotheDto clotheDto){
        // TODO: dla categryId tworzy pusty obiekt Category tylko z polem Id (reszta null)
        // NIE POBIERA NICZEGO Z BAZY
        // to wystarczy do zapisania clothe w bazie
        return mapper.map(clotheDto, Clothe.class);
    }

    public ClotheDto mapToDto(Clothe clothe){
        return mapper.map(clothe, ClotheDto.class);
    }
}
