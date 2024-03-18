package com.restapi.vinted.payload;

import com.restapi.vinted.utils.ClotheSize;
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
    @Size(min = 3, message = "Name has to be at least 3 characters")
    private String name;

    @NotNull
    @Size(min = 10, message = "Description has to be at least 10 characters")
    private String description;

    @NotNull
    @DecimalMin(value = "0.01", message = "Price has to be positive")
    private BigDecimal price;

    @NotNull
    private ClotheSize size;

    @Size(max = 5, message = "You can choose maximally 5 images")
    private List<String> images;

    private long categoryId;
    private long userId;
}