package com.example.WorkWite_Repo_BE.controllers;

import com.example.WorkWite_Repo_BE.config.payment.VNPAYConfig;
import com.example.WorkWite_Repo_BE.services.UserBalanceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class VnpayPaymentController {

    @Autowired
    private VNPAYConfig vnpayConfig;

    @Autowired
    private UserBalanceService userBalanceService;

    // Endpoint tạo link thanh toán
    @GetMapping("/api/vnpay/create-payment")
    @ResponseBody
    public String createPayment(@RequestParam(name = "amount") long amount,
                                @RequestParam(name = "userId") Long userId,   // ✅ thêm userId vào request
                                @RequestParam(name = "orderInfo", defaultValue = "Nap tien ao") String orderInfo,
                                HttpServletRequest request) {
        try {
            Map<String, String> vnp_Params = vnpayConfig.getVNPayBaseParams();

            // Bắt buộc: số tiền *100
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
            vnp_Params.put("vnp_TxnRef", VNPAYConfig.getRandomNumber(8));

            // ✅ Truyền userId vào orderInfo
            vnp_Params.put("vnp_OrderInfo", orderInfo + "|userId=" + userId);
            vnp_Params.put("vnp_IpAddr", getIpAddress(request));

            // Build query & hash
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    query.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String vnp_SecureHash = hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            return vnpayConfig.getVnp_PayUrl() + "?" + query.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Endpoint nhận kết quả trả về
    @GetMapping("/api/vnpay/return")
    @ResponseBody
    public String vnpayReturn(@RequestParam Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            Map<String, String> filteredParams = new HashMap<>(params);
            filteredParams.remove("vnp_SecureHash");

            List<String> fieldNames = new ArrayList<>(filteredParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = filteredParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                }
            }

            String myHash = hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());

            String redirectUrl;
            if (myHash.equalsIgnoreCase(vnp_SecureHash)) {
                // Parse userId từ orderInfo
                String orderInfo = params.get("vnp_OrderInfo");
                String userIdStr = null;
                if (orderInfo != null && orderInfo.contains("|userId=")) {
                    String[] parts = orderInfo.split("\\|userId=");
                    if (parts.length == 2) {
                        userIdStr = parts[1];
                    }
                }

                String amountStr = params.get("vnp_Amount");
                if (userIdStr != null && amountStr != null) {
                    long userId = Long.parseLong(userIdStr);
                    long amount = Long.parseLong(amountStr) / 100;
                    // ✅ Cộng tiền vào balance
                    userBalanceService.addBalance(userId, amount);
                    // Redirect về FE với thông tin giao dịch
                    redirectUrl = "http://localhost:5173/deposit/success?userId=" + userId + "&amount=" + amount;
                } else {
                    redirectUrl = "http://localhost:5173/deposit/success";
                }
            } else {
                redirectUrl = "http://localhost:5173/deposit/fail";
            }
            // Trả về script redirect để FE tự render UI đẹp
            return "<script>window.location.href='" + redirectUrl + "';</script>";
        } catch (Exception e) {
            return "<script>window.location.href='http://localhost:5173/deposit/fail?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8) + "';</script>";
        }
    }

    // Hàm tạo HMAC SHA512
    private String hmacSHA512(String key, String data) throws Exception {
        javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
        javax.crypto.spec.SecretKeySpec secretKeySpec =
                new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKeySpec);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}


