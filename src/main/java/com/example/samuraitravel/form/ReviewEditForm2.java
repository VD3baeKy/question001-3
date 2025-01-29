package com.example.samuraitravel.form;

import java.io.Serializable;

import lombok.Data;
 
 @Data
 public class ReviewEditForm2 implements Serializable {
	 private String contentChange;
	 private String ratingChange;
 }
 
 /*
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
 */