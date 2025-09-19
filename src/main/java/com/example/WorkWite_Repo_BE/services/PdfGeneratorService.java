package com.example.WorkWite_Repo_BE.services;

import com.example.WorkWite_Repo_BE.entities.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;


import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public byte[] generateResumePdf(Resume resume) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            // ===== HEADER =====
            Paragraph name = new Paragraph(resume.getFullName())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold();

            Paragraph jobTitle = new Paragraph(resume.getJobTitle() != null ? resume.getJobTitle() : "")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.GRAY);

            Paragraph contact = new Paragraph(
                    (resume.getEmail() != null ? resume.getEmail() : "") +
                            " | " +
                            (resume.getPhone() != null ? resume.getPhone() : ""))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setFontColor(ColorConstants.DARK_GRAY);

            document.add(name);
            document.add(jobTitle);
            document.add(contact);
            document.add(new Paragraph("\n")); // spacer

            // ===== SUMMARY =====
            addSectionTitle(document, "Summary");
            if (resume.getSummary() != null) {
                document.add(new Paragraph(resume.getSummary()).setMarginBottom(10));
            }

            // ===== SKILLS =====
            if (resume.getSkillsResumes() != null && !resume.getSkillsResumes().isEmpty()) {
                addSectionTitle(document, "Skills");
                List list = new List(ListNumberingType.DECIMAL);
                for (String skill : resume.getSkillsResumes()) {
                    list.add(new ListItem(skill));
                }
                document.add(list.setMarginBottom(10));
            }

            // ===== EDUCATION =====
            if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
                addSectionTitle(document, "Education");
                Table eduTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                        .useAllAvailableWidth();
                for (Education edu : resume.getEducations()) {
                    String time = edu.getStartYear() + " - " + edu.getEndYear();
                    eduTable.addCell(makeCell(time, true));
                    eduTable.addCell(makeCell(edu.getSchoolName() + " | " + edu.getDegree(), false));
                }
                document.add(eduTable.setMarginBottom(10));
            }

            // ===== EXPERIENCE =====
            if (resume.getExperiences() != null && !resume.getExperiences().isEmpty()) {
                addSectionTitle(document, "Experience");
                Table expTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                        .useAllAvailableWidth();
                for (Experience exp : resume.getExperiences()) {
                    String time = exp.getStartYear() + " - " + exp.getEndYear();
                    expTable.addCell(makeCell(time, true));
                    expTable.addCell(makeCell(exp.getCompanyName() + " | " + exp.getPosition() +
                            (exp.getDescription() != null ? "\n" + exp.getDescription() : ""), false));
                }
                document.add(expTable.setMarginBottom(10));
            }

            // ===== AWARDS =====
            if (resume.getAwards() != null && !resume.getAwards().isEmpty()) {
                addSectionTitle(document, "Awards");
                for (Award award : resume.getAwards()) {
                    document.add(new Paragraph("• " + award.getAwardName() +
                            (award.getAwardYear() != null ? " (" + award.getAwardYear() + ")" : ""))
                            .setMarginLeft(10));
                }
                document.add(new Paragraph("\n"));
            }

            // ===== ACTIVITIES =====
            if (resume.getActivities() != null && !resume.getActivities().isEmpty()) {
                addSectionTitle(document, "Activities");
                for (Activity act : resume.getActivities()) {
                    document.add(new Paragraph("• " + act.getActivityName())
                            .setMarginLeft(10));
                }
                document.add(new Paragraph("\n"));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    // Helper: section title
    private void addSectionTitle(Document document, String title) {
        Paragraph p = new Paragraph(title)
                .setBold()
                .setFontSize(12)
                .setMarginTop(10)
                .setMarginBottom(5)
                .setBorderBottom(new SolidBorder(ColorConstants.LIGHT_GRAY, 1));
        document.add(p);
    }

    // Helper: table cell
    private Cell makeCell(String content, boolean bold) {
        Paragraph p = new Paragraph(content).setFontSize(10);
        if (bold) p.setBold();
        return new Cell().add(p).setBorder(Border.NO_BORDER).setPadding(3);
    }
}
