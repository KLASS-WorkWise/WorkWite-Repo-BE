package com.example.WorkWite_Repo_BE.dto;

public class BannerResponse {
    private String company;
    private String position;
    private String startDate;
    private String endDate;
    private String status;
    private String imageUrl;

    public BannerResponse(String company, String position, String startDate, String endDate, String status, String imageUrl) {
        this.company = company;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}