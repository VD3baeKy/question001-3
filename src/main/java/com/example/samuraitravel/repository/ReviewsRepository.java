package com.example.samuraitravel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.samuraitravel.entity.Review;

import jakarta.transaction.Transactional;

public interface ReviewsRepository extends JpaRepository<Review, Integer> {
    //public Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    //public Page<Review> findByHouseOrderByCreatedAtDesc(House house, Pageable pageable);
    //public Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    //public Page<Review> findByUserAndHouseOrderByCreatedAtDesc(User user, House house, Pageable pageable);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.id = :reviewId")
    void deleteById(Integer id);
    
    public List<Review> findTop10ByOrderByCreatedAtDesc();
    public List<Review> findByHouseIdOrderByCreatedAtDesc(Integer houseId);
    //public Optional<Review> findById(Integer id);
}