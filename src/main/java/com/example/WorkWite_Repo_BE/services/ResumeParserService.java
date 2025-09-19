package com.example.WorkWite_Repo_BE.services;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeParserService {

    private final Tika tika = new Tika();

    /**
     * Đọc text từ file PDF/DOC/DOCX
     */
    public String extractText(MultipartFile file) {
        try (InputStream input = file.getInputStream()) {
            return tika.parseToString(input);
        } catch (IOException | TikaException e) {
            throw new RuntimeException("Không thể đọc nội dung CV", e);
        }
    }

    /**
     * Lấy ra danh sách kỹ năng có trong text (so khớp từ điển skill)
     */
    public List<String> extractSkills(String text) {
        // TODO: bạn có thể load từ DB hoặc file config thay vì hardcode
        List<String> skillDictionary = Arrays.asList(
                "java", "spring", "spring boot", "hibernate",
                "javascript", "typescript", "react", "angular", "vue",
                "nodejs", "express",
                "sql", "mysql", "postgresql", "mongodb",
                "aws", "docker", "kubernetes", "git"
        );

        String lowerText = text.toLowerCase();
        List<String> found = new ArrayList<>();
        for (String skill : skillDictionary) {
            if (lowerText.contains(skill.toLowerCase())) {
                found.add(skill);
            }
        }
        return found;
    }

    /**
     * Lấy số năm kinh nghiệm từ text (ví dụ "3 năm", "5+ years")
     */
    public long extractExperienceYears(String text) {
        String lower = text.toLowerCase();
        long maxYears = 0;

        // regex tiếng Việt: "x năm"
        Pattern vn = Pattern.compile("(\\d+)\\s*năm");
        Matcher mvn = vn.matcher(lower);
        while (mvn.find()) {
            maxYears = Math.max(maxYears, Long.parseLong(mvn.group(1)));
        }

        // regex tiếng Anh: "x years"
        Pattern en = Pattern.compile("(\\d+)\\+?\\s*(year|years)");
        Matcher men = en.matcher(lower);
        while (men.find()) {
            maxYears = Math.max(maxYears, Long.parseLong(men.group(1)));
        }

        return maxYears;
    }
}
