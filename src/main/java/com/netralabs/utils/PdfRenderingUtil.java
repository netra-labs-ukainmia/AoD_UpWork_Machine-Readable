package com.netralabs.utils;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.tagutils.TagReference;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PdfRenderingUtil {

  private static final Logger logger = LoggerFactory.getLogger(PdfRenderingUtil.class);

  private  static final PdfExtGState INVISIBLE_TEXT = new PdfExtGState().setFillOpacity(0.0f);


  /**
   * Renders a single line of text on the given PdfCanvas.
   */
  private static void renderTextLine(PdfCanvas canvas, PdfFont font, JSONObject textObj, PdfPage pdfPage) {
    String text = (String) textObj.get("text");
    if (text == null || text.isBlank()) {
      logger.warn("Skipping: Empty text in '{}'.", textObj.get("tag"));
      return;
    }

    float[] position = PositionUtil.calculateBoundingBox(textObj, pdfPage);
    float textX = position[0];
    float textY = position[1];
    float textW = position[2];
    float textH = position[3];
    float fontSize = PositionUtil.calculateBestFitFontSize(textObj, pdfPage);
    float charSpace = PositionUtil.calculateCharSpacing(font, text, fontSize, textW);
    float textWidth = font.getWidth(text, fontSize);
    float textHeight = fontSize;


    logger.info("Rendering text '{}' at ({}, {}) with size {} (Invisible)", text, textX, textY, fontSize);

    canvas.beginText()
        .setFontAndSize(font, fontSize)
        .setTextMatrix(textX, textY)
        .setCharacterSpacing(charSpace)
        .setLineWidth(textW)
        .setExtGState(INVISIBLE_TEXT)
        .showText(text)
        .endText();
  }

  /**
   * Renders invisible text inside a PDF.
   */
  public static void renderText(TagTreePointer pointer, JSONObject textObj, PdfPage pdfPage) {
    try {
      PdfCanvas canvas = new PdfCanvas(pdfPage);
      PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

      canvas.openTag(pointer.getTagReference());
      canvas.setExtGState(INVISIBLE_TEXT);
      renderTextLine(canvas, font, textObj, pdfPage);
      canvas.closeTag();
    } catch (IOException e) {
      throw new RuntimeException("Error rendering text", e);
    }
  }

  /**
   * Renders multiple lines of text (paragraph).
   */
  public static void renderParagraph(TagReference tagRef, JSONObject tagObj, PdfPage pdfPage) {
    try {
      PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
      PdfCanvas canvas = new PdfCanvas(pdfPage);
      canvas.openTag(tagRef);

      JSONArray lines = (JSONArray) tagObj.get("lines");
      for (Object lineObj : lines) {
        JSONObject line = (JSONObject) lineObj;
        renderTextLine(canvas, font, line, pdfPage);
      }

      canvas.closeTag();
    } catch (IOException e) {
      throw new RuntimeException("Error rendering paragraph", e);
    }
  }

  /**
   * Creates a tag in the hierarchy.
   */
  public static TagTreePointer createTag(TagTreePointer parentPointer, String role, PdfPage pdfPage) {
    TagTreePointer tagPointer = parentPointer.addTag(role);
    tagPointer.setPageForTagging(pdfPage);
    return tagPointer;
  }

}
