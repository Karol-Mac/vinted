package com.restapi.vinted.payload;

import com.restapi.vinted.utils.Constant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private long id;

    @Size(min = 3, message = Constant.NAME_VALIDATION_FAILED)
    @NotNull(message = "category name can not be null")
    private String name;
}
