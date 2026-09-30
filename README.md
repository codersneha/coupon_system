# 🎟️ Coupon System

A Spring Boot REST API for creating, validating and redeeming discount coupons, with usage limits and redemption tracking.

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat&logo=springboot&logoColor=white) ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)

## Features
- Create coupons with a discount type and status
- List coupons filtered by status, with offset/size pagination
- Validate a coupon before checkout, then redeem it
- Per-coupon usage limits, enforced on every redemption
- Custom exceptions for duplicates, missing coupons and exceeded limits

## API — `/api/v1/coupon`
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/` | Create a coupon |
| `GET` | `/` | List coupons (filter by status, paginated) |
| `GET` | `/{id}` | Get coupon details |
| `PATCH` | `/{id}` | Update a coupon |
| `POST` | `/validate` | Check whether a coupon can be applied |
| `POST` | `/redeem` | Redeem a coupon |

## Tech
Spring Boot · Spring Data JPA · MySQL · Lombok · Maven

## Run locally
```bash
# set your MySQL credentials in src/main/resources/application.yml
./mvnw spring-boot:run
```
