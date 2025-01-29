package com.example.samuraitravel.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
 
 @Data
 @AllArgsConstructor
public class ReviewEditForm {
    @NotNull
    private Integer id;    

    @NotNull(message = "レーティングを入力してください。")
    private Integer rating;

    @NotBlank(message = "レビューを入力してください。")
    private String reviewText;
        
    private MultipartFile imageFile;
}
 