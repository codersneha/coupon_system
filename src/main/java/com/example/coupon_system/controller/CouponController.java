package com.example.coupon_system.controller;

import com.example.coupon_system.dto.*;
import com.example.coupon_system.model.Coupon;
import com.example.coupon_system.model.CouponStatus;
import com.example.coupon_system.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupon")
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    public String createCoupon(@RequestBody CreateCouponRequestDto createCouponRequestDto) {
        return couponService.createCoupon(createCouponRequestDto);
    }

    @GetMapping
    public List<Coupon> getAllCoupons(@RequestParam(value = "couponStatus", defaultValue = "ACTIVE") CouponStatus status,
                                              @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                              @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        return couponService.getCouponsByStatus(status, offset, limit);
    }

    @GetMapping("/{id}")
    public Coupon getCouponDetails(@PathVariable("id") int id) {
        return couponService.getCouponDetails(id);
    }

    @PatchMapping("/{id}")
    public UpdateCouponResponseDto updateCoupon(@PathVariable("id") int id, @RequestBody UpdateCouponRequestDto updateCouponRequestDto) {
        return couponService.updateCoupon(id, updateCouponRequestDto);
    }

    @PostMapping("/validate")
    public ValidateCouponResponseDto validateCoupon(@RequestBody ValidateCouponRequestDto validateCouponRequestDto){
        return couponService.validateCoupon(validateCouponRequestDto);
    }
    @PostMapping("/redeem")
    public String redeemCoupon(@RequestBody ValidateCouponRequestDto validateCouponRequestDto){
        return couponService.redeemCoupon(validateCouponRequestDto);
    }

}
