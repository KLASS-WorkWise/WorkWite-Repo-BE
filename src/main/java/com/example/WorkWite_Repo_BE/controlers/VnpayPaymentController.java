package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.services.VnpayPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class VnpayPaymentController {

    @Autowired
    private VnpayPaymentService vnpayPaymentService;

    // Endpoint tạo link thanh toán
    @GetMapping("/api/vnpay/create-payment")
    @ResponseBody
    public String createPayment(@RequestParam(name = "amount") long amount,
                                @RequestParam(name = "userId") Long userId,
                                @RequestParam(name = "orderInfo", defaultValue = "Nap tien ao") String orderInfo,
                                HttpServletRequest request) {
        return vnpayPaymentService.createPayment(amount, userId, orderInfo, request);
    }

    // Endpoint nhận kết quả trả về
    @GetMapping("/api/vnpay/return")
    @ResponseBody
    public String vnpayReturn(@RequestParam Map<String, String> params) {
        return vnpayPaymentService.vnpayReturn(params);
    }

}


