package com.netralabs.processor.impl;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.netralabs.processor.TagProcessorInterface;
import com.netralabs.utils.PositionUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FigureProcessor implements TagProcessorInterface {

  private static final Logger logger = LoggerFactory.getLogger(FigureProcessor.class);

  @Override
  public void process(TagTreePointer pagePointer, JSONObject tagObj, PdfPage pdfPage) {
    if (!"Figure".equalsIgnoreCase((String) tagObj.get("tag"))) {
      return;
    }

    TagTreePointer figurePointer = pagePointer.addTag(StandardRoles.FIGURE);
    figurePointer.setPageForTagging(pdfPage);

    logger.info("Processing Figure tag");

    addImageToTag(figurePointer, tagObj, pdfPage);

    figurePointer.moveToParent();
  }

  private void addImageToTag(TagTreePointer tagPointer, JSONObject imageObj, PdfPage pdfPage) {
    JSONArray figureValues = (JSONArray) imageObj.get("Figure");
    if (figureValues == null || figureValues.isEmpty()) {
      logger.warn("Skipping Figure: No figure data found.");
      return;
    }

    PdfCanvas canvas = new PdfCanvas(pdfPage);
    PdfExtGState transparentState = new PdfExtGState().setFillOpacity(0.0f); // Make rectangle fully transparent
    canvas.setExtGState(transparentState);

    for (Object image : figureValues) {
      JSONObject imObj = (JSONObject) image;
      JSONObject figureSize = (JSONObject) imObj.get("bBox");

      if (figureSize == null) {
        logger.warn("Skipping Figure: Missing bounding box (bBox).");
        continue;
      }

      // Calculate figure position
      float[] position = PositionUtil.calculateBoundingBox(imObj, pdfPage);
      float x = position[0];
      float y = position[1];
      float width = position[2];
      float height = position[3];


      logger.info("Drawing Invisible Figure at ({}, {}), Width: {}, Height: {}", x, y, width, height);

      // Draw an invisible rectangle (NO BORDERS)
      canvas.openTag(tagPointer.getTagReference());
      canvas.rectangle(x, y, width, height);
      canvas.fill(); // **No stroke, so no visible borders**
      canvas.closeTag();
    }
  }
}
