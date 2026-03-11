package com.example.coupon_system.repository;

import com.example.coupon_system.model.Coupon;
import com.example.coupon_system.model.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon,Integer> {

    @Query("SELECT c FROM Coupon c WHERE c.code=:coupon_code")
    Coupon getCouponByCode(@Param("coupon_code") String code);

    @Query("SELECT c FROM Coupon c WHERE c.status = :status")
    Page<Coupon> getAllCouponByStatus(@Param("status") CouponStatus status, Pageable pageable);
}
