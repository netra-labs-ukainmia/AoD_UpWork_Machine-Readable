package com.netralabs.processor;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.netralabs.service.JsonParserService;
import com.netralabs.service.TaggingService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class PdfProcessor {

  private static final Logger logger = LoggerFactory.getLogger(PdfProcessor.class);

  public void processPDF(String sourcePdf, String outputPdf) {
    logger.info("Starting PDF Processing: {}", sourcePdf);

    try (PdfDocument srcPdfDoc = new PdfDocument(new PdfReader(sourcePdf));
         PdfDocument destPdfDoc = new PdfDocument(new PdfWriter(outputPdf));
         Document document = new Document(destPdfDoc)) {

      destPdfDoc.setTagged();

      int numPages = srcPdfDoc.getNumberOfPages();
      for (int i = 1; i <= numPages; i++) {
        PdfPage srcPage = srcPdfDoc.getPage(i);
        PdfPage newPage = destPdfDoc.addNewPage(new PageSize(srcPage.getPageSize()));

        PdfFormXObject pageCopy = srcPage.copyAsFormXObject(destPdfDoc);
        PdfCanvas canvas = new PdfCanvas(newPage);
        canvas.addXObjectAt(pageCopy, 0, 0);

        processPageTags(destPdfDoc, document, newPage, srcPage);
      }
      logger.info("Tagged PDF successfully created: {}", outputPdf);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processPageTags(PdfDocument destPdfDoc, Document document, PdfPage newPage, PdfPage srcPage) {
    TagTreePointer tagPointer = destPdfDoc.getTagStructureContext().getAutoTaggingPointer();
    tagPointer.moveToRoot();
    TagTreePointer pagePointer = tagPointer.addTag(StandardRoles.SECT);
    pagePointer.setPageForTagging(newPage);

    String pageText = PdfTextExtractor.getTextFromPage(srcPage);
    processTextElements(document, pagePointer, pageText, newPage);
  }

  private void processTextElements(Document document, TagTreePointer pagePointer, String pageText, PdfPage pdfPage) {
    if (pageText == null || pageText.isBlank()) {
      return;
    }

    String[] lines = pageText.split("\n");
    for (String line : lines) {
      if (line.trim().isEmpty()) {
        continue;
      }

      String role = determineTagRole(line);
      TagTreePointer tagPointer = pagePointer.addTag(role);
      tagPointer.setPageForTagging(pdfPage);
      document.add(new Paragraph(line));
      tagPointer.moveToParent();
    }
  }

  private String determineTagRole(String line) {
    if (line.matches("^\\d+\\.\\s+.*")) {
      return StandardRoles.LI;
    }
    if (line.matches("^[-*]\\s+.*")) {
      return StandardRoles.LI;
    }
    if (line.matches("^[A-Z ]{3,}$")) {
      return StandardRoles.H1;
    }
    if (line.length() > 100) {
      return StandardRoles.P;
    }
    return StandardRoles.SPAN;
  }


}