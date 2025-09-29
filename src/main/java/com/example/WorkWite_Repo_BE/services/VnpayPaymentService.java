package com.example.WorkWite_Repo_BE.services;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class VnpayPaymentService {

	private final com.example.WorkWite_Repo_BE.config.payment.VNPAYConfig vnpayConfig;
	private final com.example.WorkWite_Repo_BE.services.UserBalanceService userBalanceService;

	// Tạo link thanh toán VNPAY
	public String createPayment(long amount, Long userId, String orderInfo, jakarta.servlet.http.HttpServletRequest request) {
		try {
			Map<String, String> vnp_Params = vnpayConfig.getVNPayBaseParams();
			vnp_Params.put("vnp_Amount", String.valueOf(amount * 100));
			vnp_Params.put("vnp_TxnRef", com.example.WorkWite_Repo_BE.config.payment.VNPAYConfig.getRandomNumber(8));
			vnp_Params.put("vnp_OrderInfo", orderInfo + "|userId=" + userId);
			vnp_Params.put("vnp_IpAddr", getIpAddress(request));

			List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
			Collections.sort(fieldNames);

			StringBuilder hashData = new StringBuilder();
			StringBuilder query = new StringBuilder();
			for (int i = 0; i < fieldNames.size(); i++) {
				String fieldName = fieldNames.get(i);
				String fieldValue = vnp_Params.get(fieldName);
				if (fieldValue != null && !fieldValue.isEmpty()) {
					hashData.append(fieldName).append('=')
							.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII));
					query.append(fieldName).append('=')
							.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.UTF_8));
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

	// Xử lý kết quả trả về từ VNPAY
	public String vnpayReturn(Map<String, String> params) {
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
							.append(java.net.URLEncoder.encode(fieldValue, java.nio.charset.StandardCharsets.US_ASCII));
					if (i < fieldNames.size() - 1) {
						hashData.append('&');
					}
				}
			}
			String myHash = hmacSHA512(vnpayConfig.getSecretKey(), hashData.toString());
			String redirectUrl;
			if (myHash.equalsIgnoreCase(vnp_SecureHash)) {
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
					userBalanceService.addBalance(userId, amount);
					redirectUrl = "http://localhost:5173/deposit/success?userId=" + userId + "&amount=" + amount;
				} else {
					redirectUrl = "http://localhost:5173/deposit/success";
				}
			} else {
				redirectUrl = "http://localhost:5173/deposit/fail";
			}
			return "<script>window.location.href='" + redirectUrl + "';</script>";
		} catch (Exception e) {
			return "<script>window.location.href='http://localhost:5173/deposit/fail?error=" + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8) + "';</script>";
		}
	}

	// Hàm tạo HMAC SHA512
	private String hmacSHA512(String key, String data) throws Exception {
		javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
		javax.crypto.spec.SecretKeySpec secretKeySpec =
				new javax.crypto.spec.SecretKeySpec(key.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA512");
		hmac512.init(secretKeySpec);
		byte[] bytes = hmac512.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	private String getIpAddress(jakarta.servlet.http.HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
}
