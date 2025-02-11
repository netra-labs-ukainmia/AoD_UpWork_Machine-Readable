package com.netralabs.utils;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfPage;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionUtil {

  private static final Logger logger = LoggerFactory.getLogger(PositionUtil.class);

  public static float[] calculateBoundingBox(JSONObject tagObj, PdfPage pdfPage) {
    JSONObject bBox = (JSONObject) tagObj.get("bBox");

    if (bBox == null) {
      logger.warn("No bounding box found in tag '{}'. Returning default values.", tagObj.get("tag"));
      return new float[]{0, 0, 0, 0};
    }

    float pageWidth = pdfPage.getPageSize().getWidth();
    float pageHeight = pdfPage.getPageSize().getHeight();

//    JSONObject bBox = (JSONObject) line.get("bBox");
    double ttWidth = (Double) bBox.get("Width");
    double ttHeight = (Double) bBox.get("Height");
    double ttLeft = (Double) bBox.get("Left");
    double ttTop = (Double) bBox.get("Top");
    float avgWordHeight=0f;
    if(bBox.get("avgWordHeight") != null) {
      double ttAvgWordHeight = (Double) bBox.get("avgWordHeight");
      avgWordHeight = (float) (ttAvgWordHeight * pageHeight);
    }
    float x = (float) (ttLeft * pageWidth);
    float width = (float) (ttWidth * pageWidth);
    float height = (float) (ttHeight * pageHeight);
    float y = (float) (pageHeight - (pageHeight * ttTop) - height);


    logger.info("Calculated bounding box: X={}, Y={}, Width={}, Height={}", x, y, width, height);
    return new float[]{x, y, width, height, avgWordHeight};
  }


  /**
   * Calculates the best-fit font size based on bounding box height.
   * @param textObj JSON object containing bounding box info.
   * @param pdfPage The PDF page to determine the correct scaling.
   * @return Optimal font size.
   */
  public static float calculateBestFitFontSize(JSONObject textObj, PdfPage pdfPage) {
    if (textObj == null) {
      logger.warn("No bounding box provided for font size calculation.");
      return 12; // Default font size
    }
    JSONObject bBox = (JSONObject) textObj.get("bBox");
    if (bBox == null) {
      logger.warn("No bounding box provided for font size calculation.");
      return 12; // Default font size
    }

    float pageHeight = pdfPage.getPageSize().getHeight();
    float bboxHeight = ((Number) bBox.get("Height")).floatValue() * pageHeight;
    float fontSize = bboxHeight * 0.8f; // Scale down to fit within the box

    logger.info("Calculated best-fit font size: {}", fontSize);
    return fontSize;
  }


  public static float calculateCharSpacing(PdfFont font, String text, float fontSize, float width) {
    float result = font.getWidth(text, fontSize);
    float charSpacing = 0f;
    float extraSpace = width - result;
    int gapsBetweenChars = text.length() - 1;
    if (gapsBetweenChars > 0 && extraSpace != 0f) {
      charSpacing = (extraSpace / (float) gapsBetweenChars);
    }
    return charSpacing;
  }

  public static float getStringWidth(String text, PdfFont font, float fontSize) {
    return font.getWidth(text, fontSize);
  }

  //Extract only uppercase letters from the text, if any.
  public static String extractUppercase(String text) {
    StringBuilder sb = new StringBuilder();
    for (char c : text.toCharArray()) {
      if (Character.isUpperCase(c)) {
        sb.append(c);
      }
    }
    return sb.toString();
  }



}
