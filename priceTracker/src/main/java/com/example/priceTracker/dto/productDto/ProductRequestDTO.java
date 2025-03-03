package com.example.priceTracker.dto.productDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class ProductRequestDTO {

    @Getter
    @Setter
    public static class productDto {

        @NotBlank
        String title;
        @NotBlank
        String description;
        @NotBlank
        String url;
        @NotBlank
        String platform;
        @NotNull
        Long price;
    }

}
