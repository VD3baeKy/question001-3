package com.example.samuraitravel.entity;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;

@Getter
@Entity
@Table(name = "reviews")
@Data
public class Review {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "house_id")
    private Integer houseId;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "review_text")
    private String reviewText;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    // プロパティをMapとして取得
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true); // privateフィールドにアクセス
            try {
                properties.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public int getPropertyCount() {
        return this.getClass().getDeclaredFields().length;
    }
    
    
    public String getReviewText() {
        return reviewText;
    }
    
    
    public Integer getUserid() {
        return userId;
    }
    
    public Integer getHouseid() {
        return houseId;
    }

	public Integer getUserId() {
		return userId;
	}

	public Integer getHouseId() {
		return houseId;
	}
    
    /*
    @Override
    public String toString() {  
	    return "Review{id='" + id + 
	    		",' user_id='" + user.getId() + 
	    		"', house_id='" + house.getId() +
	    		"', rating='" + rating + 
	    		"', review_text='" + review_text + 
	    		"', image_name='" + imageName + 
	    		"', created_at='" + createdAt + 
	    		"', updated_at='" + updatedAt + 
	    		"'}"; // 必要なフィールドのみを表示
    }*/
}