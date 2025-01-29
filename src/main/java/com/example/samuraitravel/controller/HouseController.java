package com.example.samuraitravel.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewsRepository;
import com.example.samuraitravel.repository.RoleRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewsService;
import com.example.samuraitravel.service.UserService;
//import com.stripe.service.ReviewService;


@Controller
@RequestMapping("/houses")
public class HouseController {
    private static final RoleRepository RoleRepository = null;
	private final HouseRepository houseRepository;
    private final UserRepository userRepository; // UserRepositoryの追加
    private final ReviewsRepository reviewsRepository; // ReviewsRepositoryの追加
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private ReviewsService reviewService;

    @Autowired
    private UserService userService; // UserService を追加
    
    public HouseController(HouseRepository houseRepository0, UserRepository userRepository0, ReviewsRepository reviewsRepository0) {
        this.houseRepository = houseRepository0;
        this.userRepository = userRepository0; // UserRepositoryの初期化
        this.reviewsRepository = reviewsRepository0; // ReviewsRepositoryの初期化
		this.roleRepository = null;
		this.passwordEncoder = null;
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,  
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model){

        Page<House> housePage;
                
        if (keyword != null && !keyword.isEmpty()) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }            
        } else if (area != null && !area.isEmpty()) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }            
        } else if (price != null) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }            
        } else {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);   
            }            
        }                
        
        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price); 
        model.addAttribute("order", order);
        
        return "houses/index";
    }
    
     @GetMapping("/{id}")
     public String show(
    		 @PathVariable(name = "id") Integer id,
    		 Model model,
    		 @AuthenticationPrincipal UserDetailsImpl userDetails) {
    	 
		 if(userDetails!=null) {
			 System.out.println(userDetails.getUserId()); // 現在ログインしているユーザーのuserId
			 System.out.println(userDetails.getPassword()); //現在ログインしているユーザーのハッシュ化されたpassword
			 System.out.println(userDetails.getUsername()); //現在ログインしているユーザーのmailアドレス
		 }else {
			 System.out.println(" ##### NOT LOGIN #####");
		 }
		 
		 ReviewsService reviewsService = new ReviewsService(reviewsRepository);
		 reviewsService.refreshReviews();
		 
    	 
		 //UserService userService = new UserService(userRepository,roleRepository,passwordEncoder);
		 model.addAttribute("doesReviewExist", userRepository.existsById(id));
		 
		 
		 
		 // 表示中houseの物件詳細データ
		 House house = houseRepository.getReferenceById(id);
		 
		 List<User> userAll = userRepository.findAll(); // 有効なユーザーのリストを取得
	 System.out.println("Userall= " + userAll);
	 System.out.println("Userall.get(0)= " + userAll.get(2).getName());
		 
		 // Houseに関連するレビューを取得
         List <Review> reviews = reviewsRepository.findByHouseIdOrderByCreatedAtDesc(id);
         Map<Integer, Map<String, String>> reviewsWithUserName = new HashMap<>();
         /* for ( Review review : reviews) { */
         for (int i = 0; i < reviews.size(); i++) {
        	 Review review = reviews.get(i);
             Map<String, String> reviewDetails = new HashMap<>();
             reviewDetails.put("content",  String.valueOf(review.getReviewText()));
             reviewDetails.put("rating", String.valueOf(review.getRating()));
             reviewDetails.put("houseid", String.valueOf(review.getHouseid()));
             reviewDetails.put("userid", String.valueOf(review.getUserid()));
             reviewDetails.put("reviewid", String.valueOf(review.getId()));

             // ユーザー情報を取得
             String user = userService.getUserNameById(review.getUserid());
             //String userName = (user != null) ? userService.getUserNameById(review.getUserid()) : "存在しません"; // nameフィールドだけを取得
             String isUserExistencee = (user != null) ? userService.getUserNameById2(review.getUserid()) : "退会ユーザー"; // nameフィールドだけを取得

     System.out.println("review[isUserExistencee]= " + isUserExistencee);
             reviewDetails.put("isUserExistencee", isUserExistencee);
     //System.out.println("reviews[fixed]= " + reviewsWithUserName.toString());
             //reviewsWithUserName.put(review.getId(), reviewDetails);
             reviewsWithUserName.put(i, reviewDetails);
         }
     System.out.println("reviews[fixed]= " + reviewsWithUserName.toString());
     System.out.println("reviews[MAP-0]= " + reviewsWithUserName.get(0).toString());
     System.out.println("reviews[MAP-1]= " + reviewsWithUserName.get(1).toString());
     System.out.println("reviews[MAP-2]= " + reviewsWithUserName.get(2).toString());
     System.out.println(" --- ");
     System.out.println("reviewsWithUserName.size()= " + reviewsWithUserName.size());
     System.out.println("reviewsWithUserName.get(0).get(\"reviewid\")= " + reviewsWithUserName.get(0).get("reviewid"));
     System.out.println("reviewsWithUserName.get(0).get(\"houseid\")= " + reviewsWithUserName.get(0).get("houseid"));
     System.out.println("reviewsWithUserName.get(0).get(\"userid\")= " + reviewsWithUserName.get(0).get("userid"));
     System.out.println("reviewsWithUserName.get(0).get(\"isUserExistencee\")= " + reviewsWithUserName.get(0).get("isUserExistencee"));
     System.out.println("reviewsWithUserName.get(0).get(\"rating\")= " + reviewsWithUserName.get(0).get("rating"));
     System.out.println("reviewsWithUserName.get(0).get(\"content\")= " + reviewsWithUserName.get(0).get("content"));
     System.out.println(" --- ");
     
         model.addAttribute("reviewsWithUserName", reviewsWithUserName);
         
         User userLogined = null;
         if(userDetails!=null) {
        	 userLogined = userRepository.getReferenceById(userDetails.getUserId());// 現在のログインユーザーのIDからユーザーデータを取得
         }
         User user = userRepository.getReferenceById(id);// 現在の宿泊施設IDからユーザーデータを取得
         if(userDetails!=null) {
     System.out.println("Current User ID: " + userDetails.getUserId());
        	 model.addAttribute("currentUserId", userDetails.getUserId());
         }
//System.out.println("   tesuto--- " + userAll.get(0).getName());
     //System.out.println("userAll.get(2).getEnabled()= " + userAll.get(2).getEnabled());

         // 現在のユーザーのレビューを取得
         boolean hasWrittenReview = userDetails.getUserId() != null && 
             reviews.stream().anyMatch(r -> r.getUserid().equals(userDetails.getUserId())); // ユーザーのレビューがあるか確認
     System.out.println("hasWrittenReview= " + hasWrittenReview);
         
         model.addAttribute("house", house);         
         model.addAttribute("reservationInputForm", new ReservationInputForm());
         
         model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
         
         model.addAttribute("reviews",reviews); // 取得した現在の宿泊施設のレビューをモデルに追加
         model.addAttribute("user", user);     // 取得した現在の宿泊施設にレビューを投稿している全てのユーザーデータをモデルに追加
         
         //List <User> users = userRepository.findAllById(null);
         //model.addAttribute("userDetails", userRepository.findAllById(null));
//userDetails.getUserId(1).getUsername();
         
         if(userDetails!=null) {
        	 model.addAttribute("currentUserId", userDetails.getUserId()); // 現在のユーザーIDをモデルに追加         
        	 model.addAttribute("hasWrittenReview", hasWrittenReview); // ユーザーのレビューがあるかどうかをモデルに追加
        	 if(userLogined!=null)model.addAttribute("userLogined", userLogined);
         }
         
         model.addAttribute("userAll", userAll);
         
         
         model.addAttribute("house", house);
         model.addAttribute("houseName", house.getName());
         model.addAttribute("reservationInputForm", new ReservationInputForm());

         return "houses/show";
     }
     
     @GetMapping("/houses/{houseId}/review/{reviewId}/delete")
     public String delete(
    		 @PathVariable(name = "houseId") Integer houseId,
    		 @PathVariable(name = "reviewId") Integer reviewId,
    		 Model model,
    		 @AuthenticationPrincipal UserDetailsImpl userDetails){
    	 
    	 reviewsRepository.deleteById(reviewId);
    	 
    	 
    	 return "houses/show";
     }
     
     /*
     @PostMapping("/houses/{houseId}/review/{reviewId}/get")
     public String get(
    		 @PathVariable(name = "houseId") Integer houseId,
    		 @PathVariable(name = "reviewId") Integer reviewId,
    		 @RequestBody ReviewDto reviewDto, // ReviewDtoは内容と評価を持つクラス
    		 Model model,
    		 @AuthenticationPrincipal UserDetailsImpl userDetails){
    	 
    	 // レビューをデータベースから取得
    	 Review existingReview = reviewsRepository.findById(reviewId).orElseThrow(() -> new NoSuchElementException("Review not found with id " + reviewId));

 	     // ユーザーがレビューの所有者であることを確認
 	     if (!existingReview.getUserid().equals(userDetails.getUserId())) {
 	         throw new AccessDeniedException("You do not have permission to edit this review.");
 	     }
 	     
 	    // モデルに必要なデータを追加
 	    model.addAttribute("review", existingReview);
 	    model.addAttribute("houseId", houseId);
 	    
    	 return "houses/show";
     }
     */
     
     //@RequestMapping("/houses/{houseId}/review/{reviewId}/edit")
     @PostMapping("/houses/{houseId}/review/{reviewId}/edit")
     public String edit (
    		 @PathVariable(name = "houseId") Integer houseId,
    		 @PathVariable(name = "reviewId") Integer reviewId,
    		 @RequestParam("contentChange") String fixContent,
    		 @RequestParam("ratingChange") Integer fixRating,
    		 @AuthenticationPrincipal UserDetailsImpl userDetails,
    		 Model model,
    		 RedirectAttributes redirectAttributes) {
    	 
    	 System.out.println("/houses/"+houseId+"/review/"+reviewId+"/edit");
    	 
    	 List <Review> reviews = reviewsRepository.findByHouseIdOrderByCreatedAtDesc(houseId);
    	 
    	 // レビューをデータベースから取得
    	 Review existingReview = reviewsRepository.findById(reviewId).orElseThrow(() -> new NoSuchElementException("Review not found with id " + reviewId));
    	 // ユーザーがレビューの所有者であることを確認
 	     if (!existingReview.getUserid().equals(userDetails.getUserId())) {
 	    	System.out.println("ユーザーがレビューの所有者であることを確認= NG");
 	         throw new AccessDeniedException("You do not have permission to edit this review.");
 	     }
 	     System.out.println("ユーザーがレビューの所有者であることを確認= OK");
 	     
 	     // レビューの内容と評価を更新
		 existingReview.setReviewText(fixContent);
		 existingReview.setRating(fixRating);
		 existingReview.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 更新日時を現在の日時に設定

 	     // 更新を保存
 	     reviewsRepository.save(existingReview);

 	     // モデルに必要なデータを追加（例: 更新後のレビューを表示するため）
 	     //model.addAttribute("review", fixContent);
 	     //model.addAttribute("houseId", houseId);
 	     redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
    	 
 	     //return "houses/show";
 	     return "redirect:/houses/{houseId}";
     }
     
     /*
     @PostMapping("/houses/{houseId}/review/{reviewId}/edit")
     public String edit(
    		 @PathVariable(name = "houseId") Integer houseId,
    		 @PathVariable(name = "reviewId") Integer reviewId,
    		 @RequestBody ReviewDto reviewDto, // ReviewDtoは内容と評価を持つクラス
    		 Model model,
    		 @AuthenticationPrincipal UserDetailsImpl userDetails){
    	 
    	// レビューをデータベースから取得
    	    //Review existingReview = reviewsRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found with id " + reviewId));
    	 Review existingReview = reviewsRepository.findById(reviewId).orElseThrow(() -> new NoSuchElementException("Review not found with id " + reviewId));

    	    // ユーザーがレビューの所有者であることを確認
    	    if (!existingReview.getUserid().equals(userDetails.getUserId())) {
    	        throw new AccessDeniedException("You do not have permission to edit this review.");
    	    }

    	    // レビューの内容と評価を更新
    	    existingReview.setReviewText(reviewDto.getContent());
    	    existingReview.setRating(reviewDto.getRating());
    	    existingReview.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 更新日時を現在の日時に設定

    	    // 更新を保存
    	    reviewsRepository.save(existingReview);

    	    // モデルに必要なデータを追加（例: 更新後のレビューを表示するため）
    	    model.addAttribute("review", existingReview);
    	    model.addAttribute("houseId", houseId);

    	 
    	 
    	 return "houses/show";
     }
     */


}

