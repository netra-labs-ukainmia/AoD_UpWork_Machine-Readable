package com.netralabs.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ITextTextPositionExtractor {

  public static Map<String, List<float[]>> extractTextPositions(String filePath) throws IOException {
    Map<String, List<float[]>> textPositions = new LinkedHashMap<>();
    PdfDocument pdfDoc = new PdfDocument(new PdfReader(filePath));

    for (int pageNum = 1; pageNum <= pdfDoc.getNumberOfPages(); pageNum++) {
      PdfPage page = pdfDoc.getPage(pageNum);
      PdfTextPositionExtractorListener listener = new PdfTextPositionExtractorListener(textPositions, pageNum);
      PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
      processor.processPageContent(page);
    }

    pdfDoc.close();
    return textPositions;
  }

}
