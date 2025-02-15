package com.netralabs.processor;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.netralabs.processor.impl.TextProcessor;
import com.netralabs.service.JsonParserService;
import com.netralabs.service.TaggingService;
import com.netralabs.utils.PdfRenderingUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PdfProcessor {

  private static final Logger logger = LoggerFactory.getLogger(PdfProcessor.class);
  private static final PdfExtGState INVISIBLE_TEXT = new PdfExtGState().setFillOpacity(0.0f);


  public void processPDF(String sourcePdf, String outputPdf, Map<String, List<float[]>> textPositions) {
    logger.info("Starting PDF Processing: {}", sourcePdf);

    try (PdfDocument srcPdfDoc = new PdfDocument(new PdfReader(sourcePdf));
         PdfDocument destPdfDoc = new PdfDocument(new PdfWriter(outputPdf))) {

      destPdfDoc.setTagged(); // Ensure PDF is tagged

      int numPages = srcPdfDoc.getNumberOfPages();

      for (int i = 1; i <= numPages; i++) {
        PdfPage srcPage = srcPdfDoc.getPage(i);
        PdfPage newPage = destPdfDoc.addNewPage(new PageSize(srcPage.getPageSize()));
        PdfFormXObject pageCopy = srcPage.copyAsFormXObject(destPdfDoc);
        PdfCanvas canvas = new PdfCanvas(newPage);
        canvas.addXObjectAt(pageCopy, 0, 0);
        TagTreePointer tagPointer = destPdfDoc.getTagStructureContext().getAutoTaggingPointer();
        tagPointer.moveToRoot();
        TagTreePointer pagePointer = tagPointer.addTag(StandardRoles.SECT);
        pagePointer.setPageForTagging(newPage);
        processPageTags(pagePointer, newPage, textPositions, i);
      }
      logger.info("Tagged PDF successfully created: {}", outputPdf);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void processPageTags(
      TagTreePointer pagePointer,
      PdfPage newPage,
      Map<String, List<float[]>> textPositions,
      int pageNumber
  ) {
    List<Map.Entry<String, float[]>> sortedTextPositions = sortingMapWithText(textPositions, pageNumber);
    // Render text using PdfCanvas at correct positions
    PdfCanvas canvas = new PdfCanvas(newPage);
    for (Map.Entry<String, float[]> entry : sortedTextPositions) {
      String text = entry.getKey();
      String role = determineTagRole(text);
      TagTreePointer tagPointer = pagePointer.addTag(role);
      tagPointer.setPageForTagging(newPage);
      try {
      // Debug Log
//      logger.info("Tagged '{}' at (X: {:.2f}, Y: {:.2f}, FontSize: {:.2f}) on Page {}",
//          text, x, y, fontSize, pageNumber);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        canvas.openTag(tagPointer.getTagReference());
        canvas.setExtGState(INVISIBLE_TEXT);
        PdfRenderingUtil.renderTextValues(canvas, font,text, newPage, entry.getValue());
        canvas.closeTag();
      } catch (IOException e) {
        throw new RuntimeException("Error rendering text", e);
      }
      tagPointer.moveToParent();
    }
  }


  private List<Map.Entry<String, float[]>> sortingMapWithText(
      Map<String, List<float[]>> textPositions,
      int pageNumber
  ) {
    List<Map.Entry<String, float[]>> sortedTextPositions = new ArrayList<>();
    for (Map.Entry<String, List<float[]>> entry : textPositions.entrySet()) {
      for (float[] position : entry.getValue()) {
        if ((int) position[4] == pageNumber) { // Ensure correct page
          sortedTextPositions.add(new AbstractMap.SimpleEntry<>(entry.getKey(), position));
        }
      }
    }

    // Sort text by Y descending (top to bottom) and then by X ascending (left to right)
    sortedTextPositions.sort((a, b) -> {
      float[] posA = a.getValue();
      float[] posB = b.getValue();
      int compareY = Float.compare(posB[1], posA[1]); // Sort Y descending
      return (compareY != 0) ? compareY : Float.compare(posA[0], posB[0]); // Sort X ascending
    });
    return sortedTextPositions;
  }

//  private void processPageTags(
//      PdfDocument destPdfDoc, PdfCanvas canvas, PdfPage newPage,
//      Map<String, List<float[]>> textPositions, PdfFont font, int pageNumber
//  ) {
//    TagTreePointer tagPointer = destPdfDoc.getTagStructureContext().getAutoTaggingPointer();
//    tagPointer.moveToRoot();
//    TagTreePointer pagePointer = tagPointer.addTag(StandardRoles.SECT);
//    pagePointer.setPageForTagging(newPage);
//
//    processTextElements(canvas, pagePointer, textPositions, newPage, font, pageNumber);
//  }
//
//  private void processTextElements(
//      PdfCanvas canvas, TagTreePointer pagePointer,
//      Map<String, List<float[]>> textPositions, PdfPage pdfPage, PdfFont font, int pageNumber
//  ) {
//    List<Map.Entry<String, float[]>> sortedTextPositions = new ArrayList<>();
//
//    for (Map.Entry<String, List<float[]>> entry : textPositions.entrySet()) {
//      for (float[] position : entry.getValue()) {
//        if ((int) position[4] == pageNumber) { // Ensure correct page
//          sortedTextPositions.add(new AbstractMap.SimpleEntry<>(entry.getKey(), position));
//        }
//      }
//    }
//
//    // Sort text by Y descending (top to bottom) and then by X ascending (left to right)
//    sortedTextPositions.sort((a, b) -> {
//      float[] posA = a.getValue();
//      float[] posB = b.getValue();
//      int compareY = Float.compare(posB[1], posA[1]); // Sort Y descending
//      return (compareY != 0) ? compareY : Float.compare(posA[0], posB[0]); // Sort X ascending
//    });
//
//  }


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
    return StandardRoles.P;
  }


}