package com.careercompass.careercompass.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ResumeParserService {

    public ParseResult parse(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        try (PDDocument doc = Loader.loadPDF(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text  = stripper.getText(doc);
            int    pages = doc.getNumberOfPages();
            return new ParseResult(text, pages);
        }
    }

    public static class ParseResult {
        public final String text;
        public final int    pageCount;

        public ParseResult(String text, int pageCount) {
            this.text      = text;
            this.pageCount = pageCount;
        }
    }
}
