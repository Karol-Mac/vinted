package com.restapi.vinted.payload;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotEmpty
    private String name;
    @Size(min = 3, max = 20, message = "username need's to contain 3-20 characters")
    @NotNull
    private String username;
    @Email(message = "email has to contain '@'.")
    @NotNull
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z]).+$", message = "must have at least 1 small letter and 1 uppercase letter")
    @Pattern(regexp = ".*\\d+.*", message = "must have at least 1 number")
    @Pattern(regexp = ".*[\\W_]+.*", message = "must have at least 1 special character")
    @Pattern(regexp = "^.{8,20}$", message = "must be at least 8 characters (max 20)")
    private String password;
}


//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message = "must have at least: 1 small letter, 1 uppercase letter, 1 number, " +
//                    "1 special character & at least 8 characters (total) ")