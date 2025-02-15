package com.netralabs.utils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.ImageRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PdfTextPositionExtractorListener implements ITextExtractionStrategy {

  private final Map<String, List<float[]>> textPositions;
  private final int pageNumber;


  public PdfTextPositionExtractorListener(Map<String, List<float[]>> textPositions, int pageNumber) {
    this.textPositions = textPositions;
    this.pageNumber = pageNumber;
  }


  @Override
  public String getResultantText() {
    return "";
  }

  @Override
  public void eventOccurred(IEventData data, EventType type) {
    if (type.equals(EventType.RENDER_TEXT) && data instanceof TextRenderInfo) {
      TextRenderInfo renderInfo = (TextRenderInfo) data;
      String text = renderInfo.getText();
      Rectangle rect = renderInfo.getBaseline().getBoundingRectangle();

      if (rect != null && text != null && !text.trim().isEmpty()) {
        float x = rect.getX();
        float y = rect.getY();
        float width = rect.getWidth();
        float height = rect.getHeight();
        textPositions.computeIfAbsent(text, k -> new ArrayList<>()).add(new float[]{x, y, width, height, pageNumber});
        System.out.printf("Text: '%s' at (X: %.2f, Y: %.2f, Width: %.2f, Height: %.2f) on Page %d%n",
            text, x, y, width, height, pageNumber);
      }
    }
  }

  @Override
  public Set<EventType> getSupportedEvents() {
    return Collections.singleton(EventType.RENDER_TEXT);
  }

//
//  @Override
//  public void endTextBlock() {}
//
//  @Override
//  public void renderImage(ImageRenderInfo renderInfo) {}
//
//  @Override
//  public String getResultantText() {
//    return null;
//  }

}
