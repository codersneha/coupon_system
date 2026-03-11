package com.example.coupon_system.repository;

import com.example.coupon_system.model.Coupon;
import com.example.coupon_system.model.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption,Integer> {
    @Query("SELECT r FROM CouponRedemption r WHERE r.code=:coupon_code")
    CouponRedemption getRedemptionByCode(@Param("coupon_code") String code);
}
