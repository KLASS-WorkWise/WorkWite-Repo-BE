package com.example.WorkWite_Repo_BE.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
     * Kiểm tra PDF có chứa text layer hay chỉ là scan
     */



    private static final Map<String, String> SKILL_ALIASES = Map.ofEntries(
            // JavaScript ecosystem
            Map.entry("js", "javascript"),
            Map.entry("javascript", "javascript"),
            Map.entry("ecmascript", "javascript"),
            Map.entry("nodejs", "nodejs"),
            Map.entry("node", "nodejs"),
            Map.entry("expressjs", "nodejs"),
            Map.entry("ts", "typescript"),
            Map.entry("typescript", "typescript"),

            // React ecosystem
            Map.entry("reactjs", "react"),
            Map.entry("react", "react"),
            Map.entry("nextjs", "react"),
            Map.entry("redux", "react"),

            // Spring / Java
            Map.entry("springboot", "spring"),
            Map.entry("spring boot", "spring"),
            Map.entry("spring framework", "spring"),
            Map.entry("hibernate orm", "hibernate"),

            // SQL / Database
            Map.entry("sql", "sql"),
            Map.entry("mysql", "sql"),
            Map.entry("postgresql", "sql"),
            Map.entry("postgres", "sql"),
            Map.entry("mssql", "sql"),
            Map.entry("oracle", "sql"),
            Map.entry("sqlite", "sql"),
            Map.entry("nosql", "nosql"),
            Map.entry("mongodb", "nosql"),
            Map.entry("cassandra", "nosql"),
            Map.entry("dynamodb", "nosql"),

            // Cloud
            Map.entry("amazon web services", "aws"),
            Map.entry("aws", "aws"),
            Map.entry("azure cloud", "azure"),
            Map.entry("gcp", "gcp"),
            Map.entry("google cloud", "gcp"),

            // DevOps
            Map.entry("k8s", "kubernetes"),
            Map.entry("kubernetes", "kubernetes"),
            Map.entry("docker-compose", "docker"),
            Map.entry("ci/cd", "devops"),
            Map.entry("jenkins pipeline", "jenkins"),

            // Programming languages (aliases / abbreviations)
            Map.entry("c++", "c++"),
            Map.entry("cpp", "c++"),
            Map.entry("c#", "c#"),
            Map.entry("c sharp", "c#"),
            Map.entry("py", "python"),
            Map.entry("python", "python"),
            Map.entry("golang", "go"),
            Map.entry("go", "go"),
            Map.entry("jsf", "java"),
            Map.entry("jsp", "java"),

            // Mobile
            Map.entry("android sdk", "android"),
            Map.entry("ios", "ios"),
            Map.entry("swiftui", "swift"),
            Map.entry("objective-c", "objective-c"),
            Map.entry("rn", "react native"),
            Map.entry("react native", "react native"),

            // Tools
            Map.entry("gitlab ci", "gitlab ci"),
            Map.entry("github actions", "github actions"),
            Map.entry("jira software", "jira"),
            Map.entry("confluence wiki", "confluence")
    );

    /**
     * Lấy ra danh sách kỹ năng có trong text (so khớp từ điển skill)
     */
    public List<String> extractSkills(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();

        List<String> skillDictionary = Arrays.asList(
                // Programming languages
                "java", "spring", "spring boot", "hibernate",
                "javascript", "typescript", "react", "angular", "vue", "nextjs", "nuxtjs",
                "nodejs", "express", "python", "django", "flask", "fastapi",
                "c", "c++", "c#", ".net", "php", "laravel", "ruby", "rails",
                "swift", "kotlin", "flutter", "dart", "objective-c", "go", "rust", "scala",
                // Databases
                "sql", "mysql", "postgresql", "oracle", "mssql", "mongodb", "nosql", "redis", "cassandra", "elasticsearch",
                // Web / Frontend
                "html", "css", "sass", "less", "bootstrap", "tailwind", "jquery",
                // DevOps & Cloud
                "docker", "kubernetes", "aws", "azure", "gcp", "jenkins", "gitlab ci", "github actions",
                "terraform", "ansible", "nginx", "apache", "linux", "bash", "shell scripting",
                // Tools & Others
                "git", "jira", "confluence", "agile", "scrum", "kanban",
                // AI / Data
                "machine learning", "deep learning", "tensorflow", "pytorch", "keras", "pandas", "numpy", "scikit-learn",
                "hadoop", "spark", "hive", "kafka", "airflow",
                // Mobile
                "android", "ios", "react native", "xamarin", "ionic",
                // Testing
                "selenium", "junit", "testng", "cypress", "playwright", "postman",
                // Security
                "oauth", "jwt", "ssl", "tls"
        );

        Set<String> foundSkills = new HashSet<>();

        for (String skill : skillDictionary) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                // Chuẩn hóa skill qua alias map (nếu có)
                String normalized = SKILL_ALIASES.getOrDefault(skill.toLowerCase(), skill.toLowerCase());
                foundSkills.add(normalized);
            }
        }

        return new ArrayList<>(foundSkills);
    }

    public long extractExperienceYears(String text) {
        if (text == null || text.isBlank()) return 0;

        List<ExperiencePeriod> periods = new ArrayList<>();

        // Regex bắt range: 01/2018 - 05/2020, 2017 - 2021, 2020 - Present
        Pattern pattern = Pattern.compile(
                "(\\d{2}/\\d{4}|\\d{4})\\s*(-|to)\\s*(\\d{2}/\\d{4}|\\d{4}|Present|Hiện tại)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            LocalDate start = parseDate(matcher.group(1));
            LocalDate end = parseDate(matcher.group(3));
            if (start != null && end != null && !end.isBefore(start)) {
                periods.add(new ExperiencePeriod(start, end));
            }
        }

        // Nếu có khoảng thời gian thì tính tổng
        if (!periods.isEmpty()) {
            return calculateTotalExperienceYears(periods);
        }

        // Fallback: tìm "X years of experience" hoặc "X năm"
        Pattern yearPattern = Pattern.compile("(\\d+)\\s*(years|year|năm)", Pattern.CASE_INSENSITIVE);
        Matcher yearMatcher = yearPattern.matcher(text);
        if (yearMatcher.find()) {
            return Long.parseLong(yearMatcher.group(1));
        }

        return 0;
    }

    private LocalDate parseDate(String input) {
        if (input == null) return null;
        input = input.trim();
        if (input.equalsIgnoreCase("Present") || input.equalsIgnoreCase("Hiện tại")) {
            return LocalDate.now();
        }
        try {
            if (input.matches("\\d{2}/\\d{4}")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
                return YearMonth.parse(input, formatter).atDay(1);
            } else if (input.matches("\\d{4}")) {
                return LocalDate.of(Integer.parseInt(input), 1, 1);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private long calculateTotalExperienceYears(List<ExperiencePeriod> periods) {
        if (periods.isEmpty()) return 0;

        // Sắp xếp theo start date
        periods.sort(Comparator.comparing(ExperiencePeriod::getStart));

        List<ExperiencePeriod> merged = new ArrayList<>();
        ExperiencePeriod current = periods.get(0);

        for (int i = 1; i < periods.size(); i++) {
            ExperiencePeriod next = periods.get(i);

            // Nếu overlap hoặc liền kề
            if (!next.getStart().isAfter(current.getEnd())) {
                // Gộp lại: end = max(end1, end2)
                LocalDate newEnd = current.getEnd().isAfter(next.getEnd()) ? current.getEnd() : next.getEnd();
                current = new ExperiencePeriod(current.getStart(), newEnd);
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        // Tính tổng tháng sau khi merge
        long totalMonths = 0;
        for (ExperiencePeriod p : merged) {
            totalMonths += ChronoUnit.MONTHS.between(p.getStart(), p.getEnd());
        }

        return totalMonths / 12;
    }

    class ExperiencePeriod {
        private LocalDate start;
        private LocalDate end;
        public ExperiencePeriod(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
        public LocalDate getStart() { return start; }
        public LocalDate getEnd() { return end; }
    }

    // ----------------- ✅ Thêm 2 hàm tính skill match -----------------

    /**
     * Tính danh sách kỹ năng bị thiếu (job yêu cầu nhưng CV không có)
     */
    public List<String> calculateMissingSkills(List<String> requiredSkills, List<String> candidateSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return Collections.emptyList();
        if (candidateSkills == null) candidateSkills = Collections.emptyList();

        Set<String> candidateSet = new HashSet<>();
        for (String skill : candidateSkills) {
            candidateSet.add(skill.toLowerCase().trim());
        }

        List<String> missing = new ArrayList<>();
        for (String req : requiredSkills) {
            if (!candidateSet.contains(req.toLowerCase().trim())) {
                missing.add(req);
            }
        }
        return missing;
    }

    /**
     * Tính % match giữa kỹ năng của ứng viên và yêu cầu công việc
     */
    public double calculateSkillMatchPercent(List<String> requiredSkills, List<String> candidateSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) return 100.0;
        if (candidateSkills == null) candidateSkills = Collections.emptyList();

        Set<String> candidateSet = new HashSet<>();
        for (String skill : candidateSkills) {
            candidateSet.add(skill.toLowerCase().trim());
        }

        int matched = 0;
        for (String req : requiredSkills) {
            if (candidateSet.contains(req.toLowerCase().trim())) {
                matched++;
            }
        }

        return ((double) matched / requiredSkills.size()) * 100.0;
    }
}
