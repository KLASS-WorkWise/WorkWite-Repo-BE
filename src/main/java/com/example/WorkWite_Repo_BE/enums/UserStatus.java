//package com.example.WorkWite_Repo_BE.enums;
//
//import lombok.Getter;
//
//@Getter
//public class UserStatus {
//    ACTIVE("ACT"), INACTIVE("INA"), SUSPENDED("SUS");
//
//    private final String code;
//
//    StudentStatus(String code) {
//        this.code = code;
//    }
//
//    public static UserStatus fromCode(String code) {
//        for (UserStatus s : UserStatus.values()) {
//            if (s.code.equals(code))
//                return s;
//        }
//        throw new IllegalArgumentException("Invalid code: " + code);
//    }
//}
