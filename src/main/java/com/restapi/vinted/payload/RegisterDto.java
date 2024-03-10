package com.restapi.vinted.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotEmpty(message = "name must not be empty")
    private String name;
    @Size(min = 3, max = 20, message = "username need's to contain 3-20 characters")
    private String username;
    @Email(message = "email has to contain '@'.")
    private String email;
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message = "must have at least: 1 small letter, 1 uppercase letter, 1 number, " +
//                    "1 special character & at least 8 characters (total) ")
    private String password;
}
