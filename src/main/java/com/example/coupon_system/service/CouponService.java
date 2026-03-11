package com.example.coupon_system.service;

import com.example.coupon_system.dto.*;
import com.example.coupon_system.exception.CouponAlreadyExistsException;
import com.example.coupon_system.exception.CouponNotFoundException;
import com.example.coupon_system.exception.CouponUsageLimitExceededException;
import com.example.coupon_system.model.Coupon;
import com.example.coupon_system.model.CouponRedemption;
import com.example.coupon_system.model.CouponStatus;
import com.example.coupon_system.repository.CouponRedemptionRepository;
import com.example.coupon_system.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository couponRedemptionRepository;

    public String createCoupon(CreateCouponRequestDto createCouponRequestDto){
        Coupon couponExist= couponRepository.getCouponByCode(createCouponRequestDto.getCode());
        if (couponExist != null) {
            throw new CouponAlreadyExistsException("Coupon Already Exists!");
        }
        final Coupon coupon = Coupon.builder()
                .code(createCouponRequestDto.getCode())
                .discountType(createCouponRequestDto.getDiscountType())
                .discountValue(createCouponRequestDto.getDiscountValue())
                .minOrderAmount(createCouponRequestDto.getMinOrderAmount())
                .maxDiscountAmount(createCouponRequestDto.getMaxDiscountAmount())
                .startDate(createCouponRequestDto.getStartDate())
                .endDate(createCouponRequestDto.getEndDate())
                .usageLimit(createCouponRequestDto.getUsageLimit())
                .status(CouponStatus.ACTIVE)
                .build();
        couponRepository.save(coupon);

        return "Coupon created!";
    }

    public List<Coupon> getCouponsByStatus(CouponStatus status, int offset, int size){
        final PageRequest pageable = PageRequest.of(offset, size, Sort.by("startDate").ascending());
        return couponRepository.getAllCouponByStatus(status, pageable).stream().toList();
    }

    public Coupon getCouponDetails(int id){
        return couponRepository.findById(id).orElseThrow(()->
                new CouponNotFoundException(String.format("%s not found", id)));
    }

    public UpdateCouponResponseDto updateCoupon(int id, UpdateCouponRequestDto updateCouponRequestDto){

        //at time t it has been read
        final Coupon coupon = couponRepository.findById(id).orElseThrow(()-> new CouponNotFoundException("Coupon not found!"));
        coupon.setDiscountValue(updateCouponRequestDto.getDiscountValue() == null ? coupon.getDiscountValue(): updateCouponRequestDto.getDiscountValue());
        coupon.setMinOrderAmount(updateCouponRequestDto.getMinOrderAmount()== null ? coupon.getMinOrderAmount() : updateCouponRequestDto.getMinOrderAmount());
        coupon.setMaxDiscountAmount(updateCouponRequestDto.getMaxDiscountAmount()== null ? coupon.getMaxDiscountAmount(): updateCouponRequestDto.getMaxDiscountAmount());
        coupon.setEndDate(updateCouponRequestDto.getEndDate()==null ?  coupon.getEndDate() : updateCouponRequestDto.getEndDate());
        coupon.setUsageLimit(updateCouponRequestDto.getUsageLimit()==null ? coupon.getUsageLimit() : updateCouponRequestDto.getUsageLimit());

        Coupon savedCoupon = couponRepository.save(coupon);//it's happening at t+x second

        return new UpdateCouponResponseDto(
                savedCoupon.getId(),
                savedCoupon.getCode(),
                savedCoupon.getDiscountValue(),
                savedCoupon.getMinOrderAmount(),
                savedCoupon.getMaxDiscountAmount(),
                savedCoupon.getEndDate(),
                savedCoupon.getUsageLimit()
        );
    }

    public ValidateCouponResponseDto validateCoupon(ValidateCouponRequestDto validateCouponRequestDto){
        Coupon coupon= couponRepository.getCouponByCode(validateCouponRequestDto.getCode());

        if (coupon==null){
            return new ValidateCouponResponseDto(false, "Coupon code does not exist");
        }
        if (coupon.getEndDate().isBefore(LocalDate.now())){
            //status should be marked as xpired un db
            coupon.setStatus(CouponStatus.EXPIRED);
            couponRepository.save(coupon);

            return new ValidateCouponResponseDto(false, "Coupon has expired");
        }

        CouponRedemption redemption = couponRedemptionRepository.getRedemptionByCode(coupon.getCode());
        if (redemption != null && redemption.getTotalRedemption() >= coupon.getUsageLimit()){
            return new ValidateCouponResponseDto(false,"usage limit exceeded");
        }

        return new ValidateCouponResponseDto(true,"Coupon is valid");
    }

    public String redeemCoupon(ValidateCouponRequestDto validateCouponRequestDto){
        Coupon coupon = couponRepository.getCouponByCode(validateCouponRequestDto.getCode());
        if(coupon == null){
            throw new CouponNotFoundException("Coupon not found");
        }
        CouponRedemption redeem = couponRedemptionRepository.getRedemptionByCode(validateCouponRequestDto.getCode());
        if(redeem == null){
            redeem = CouponRedemption.builder()
                    .code(validateCouponRequestDto.getCode())
                    .totalRedemption(1)
                    .build();
        } else {
            if(redeem.getTotalRedemption() >= coupon.getUsageLimit()){
                throw new CouponUsageLimitExceededException(String.format("max redemption exceeded for coupon %s ",coupon.getCode()));
            }
            redeem.setTotalRedemption(redeem.getTotalRedemption() + 1);
        }
        couponRedemptionRepository.save(redeem);
        return "Coupon redeemed!";
    }

}
