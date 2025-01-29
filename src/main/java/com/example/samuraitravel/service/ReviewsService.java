package com.example.samuraitravel.service;
 
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
//import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.ReviewsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
 
 @Service
 public class ReviewsService {
     private final ReviewsRepository reviewsRepository;    
     
     public ReviewsService(ReviewsRepository reviewsRepository) {
         this.reviewsRepository = reviewsRepository;        
     }

     public List<Review> refreshReviews() {
    	 System.out.println(" **** cash crear ****");
         return reviewsRepository.findAll(); // 最新のデータを取得
     }
     
    /* レビュー登録機能 */
     @Transactional
     public void create(ReviewRegisterForm reviewRegisterForm) {
         Review review = new Review();        
         MultipartFile imageFile = reviewRegisterForm.getImageFile();
         
         if (!imageFile.isEmpty()) {
             String imageName = imageFile.getOriginalFilename(); 
             String hashedImageName = generateNewFileName(imageName);
             //Path filePath = Paths.get("src/main/resources/static/review_img/" + hashedImageName);
             //copyImageFile(imageFile, filePath);
             review.setImageName(hashedImageName);
         }
         
         review.setRating(reviewRegisterForm.getRating());
         review.setReviewText(reviewRegisterForm.getReviewText());

         reviewsRepository.save(review);
     }

    /* レビュー編集機能 */
     @Transactional
     public void update(ReviewEditForm reviewEditForm) {
         Review review = reviewsRepository.getReferenceById(reviewEditForm.getId());
         MultipartFile imageFile = reviewEditForm.getImageFile();
         
         if (!imageFile.isEmpty()) {
             String imageName = imageFile.getOriginalFilename(); 
             String hashedImageName = generateNewFileName(imageName);
             //Path filePath = Paths.get("src/main/resources/static/review_img/" + hashedImageName);
             //copyImageFile(imageFile, filePath);
             review.setImageName(hashedImageName);
         }
         
         review.setRating(reviewEditForm.getRating());
         review.setReviewText(reviewEditForm.getReviewText());

         reviewsRepository.save(review);
     } 
     
     // UUIDを使って生成したファイル名を返す
     public String generateNewFileName(String fileName) {
         String[] fileNames = fileName.split("\\.");                
         for (int i = 0; i < fileNames.length - 1; i++) {
             fileNames[i] = UUID.randomUUID().toString();            
         }
         String hashedFileName = String.join(".", fileNames);
         return hashedFileName;
     }
     
     // 画像ファイルを指定したファイルにコピーする
     /*
     public void copyImageFile(MultipartFile imageFile, Path filePath) {           
         try {
             Files.copy(imageFile.getInputStream(), filePath);
         } catch (IOException e) {
             e.printStackTrace();
         }          
     } 
     */
     
     // キャッシュをクリア
         @PersistenceContext
         private EntityManager entityManager;
         public void clearCache() {
             entityManager.clear(); 
         }
     
 }