package com.student.management.security.services.impl;

import com.student.management.payload.response.CourseDto;
import com.student.management.security.services.PdfGeneratorService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final float FONT_SIZE = 12;
    private static final PDType1Font FONT = PDType1Font.TIMES_ROMAN;
    private static final float LEADING = 1.5f * FONT_SIZE;
    private static final float MARGIN = 50;
    private static final float WIDTH = 500;

    @Override
    public byte[] generateCourseSchedulePdf(List<CourseDto> courses) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.setFont(FONT, FONT_SIZE);

            float y = page.getMediaBox().getHeight() - MARGIN;

            writeText(contentStream, "Course Schedule", MARGIN, y);
            y -= 2 * LEADING;

            for (CourseDto course : courses) {
                writeText(contentStream, "Title: " + course.getTitle(), MARGIN, y);
                y -= LEADING;
                writeText(contentStream, "Description: " + course.getDescription(), MARGIN, y);
                y -= LEADING;
                y -= LEADING; // Extra space between courses
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream.toByteArray();
    }

    private void writeText(PDPageContentStream contentStream, String text, float x, float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }
}
