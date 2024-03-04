package com.restapi.vinted.payload;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private long id;

    @Size(min = 3, message = "Name has to be at least 3 characters")
    private String name;
}
