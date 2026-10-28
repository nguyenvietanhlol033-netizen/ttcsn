package com.melia.hotel.controller;

import com.melia.hotel.dto.request.ReviewRequest;
import com.melia.hotel.dto.response.ApiResponse;
import com.melia.hotel.entity.DanhGia;
import com.melia.hotel.security.UserPrincipal;
import com.melia.hotel.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Customer Reviews", description = "Các API tiếp nhận và hiển thị đánh giá của khách hàng")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Gửi đánh giá dịch vụ sau khi Check-out (1 - 5 sao)")
    public ResponseEntity<ApiResponse<DanhGia>> submitReview(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        DanhGia danhGia = reviewService.createReview(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cảm ơn Quý khách đã gửi đánh giá trải nghiệm dịch vụ", danhGia));
    }

    @GetMapping("/featured")
    @Operation(summary = "Xem danh sách các đánh giá tích cực (từ 4 sao trở lên)")
    public ResponseEntity<ApiResponse<List<DanhGia>>> getFeaturedReviews() {
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách đánh giá tiêu biểu thành công", reviewService.getHighRatingReviews()));
    }
}
