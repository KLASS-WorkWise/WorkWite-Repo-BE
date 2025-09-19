package com.example.WorkWite_Repo_BE.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    private final Long userId;
    private final Long balance;
    private final Long requiredAmount;
    private final String bannerType;

    public InsufficientBalanceException(Long userId, Long balance, Long requiredAmount, String bannerType) {
        super("Insufficient balance: userId=" + userId + ", balance=" + balance + ", required=" + requiredAmount + ", bannerType=" + bannerType);
        this.userId = userId;
        this.balance = balance;
        this.requiredAmount = requiredAmount;
        this.bannerType = bannerType;
    }

    public Long getUserId() { return userId; }
    public Long getBalance() { return balance; }
    public Long getRequiredAmount() { return requiredAmount; }
    public String getBannerType() { return bannerType; }
}
