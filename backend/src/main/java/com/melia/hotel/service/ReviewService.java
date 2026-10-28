package com.melia.hotel.service;

import com.melia.hotel.dto.request.ReviewRequest;
import com.melia.hotel.entity.DanhGia;
import com.melia.hotel.security.UserPrincipal;

import java.util.List;

public interface ReviewService {
    DanhGia createReview(ReviewRequest request, UserPrincipal currentUser);
    List<DanhGia> getHighRatingReviews();
}
