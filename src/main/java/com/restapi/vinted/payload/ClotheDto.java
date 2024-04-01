package com.restapi.vinted.payload;

import com.restapi.vinted.utils.ClotheSize;
import com.restapi.vinted.utils.Constant;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClotheDto {
    private long id;

    @NotNull
    @Size(min = 3, message = Constant.NAME_VALIDATION_FAILED)
    private String name;

    @NotNull
    @Size(min = 10, message = Constant.DESCRIPTION_VALIDATION_FAILED)
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = Constant.PRICE_VALIDATION_FAILED)
    private BigDecimal price;

    @NotNull
    private ClotheSize size;

    @Size(max = 5, message = Constant.IMAGES_VALIDATION_FAILED)
    private List<String> images;

    private long categoryId;
    private long userId;
}