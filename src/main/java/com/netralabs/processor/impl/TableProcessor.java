package com.netralabs.processor.impl;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagReference;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.netralabs.processor.TagProcessorInterface;
import com.netralabs.utils.PositionUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TableProcessor implements TagProcessorInterface {

  private static final Logger logger = LoggerFactory.getLogger(TableProcessor.class);

  @Override
  public void process(TagTreePointer pagePointer, JSONObject tagObj, PdfPage pdfPage) {
    if (!"Table".equalsIgnoreCase((String) tagObj.get("tag"))) {
      return;
    }

    TagTreePointer tablePointer = pagePointer.addTag(StandardRoles.TABLE);
    tablePointer.setPageForTagging(pdfPage);

    JSONArray tableCells = (JSONArray) tagObj.get("cells");
    if (tableCells == null || tableCells.isEmpty()) {
      logger.warn("Skipping Table: No cells found.");
      return;
    }

    PdfCanvas canvas = new PdfCanvas(pdfPage); // Create a single canvas instance

    for (Object cellObj : tableCells) {
      JSONObject cell = (JSONObject) cellObj;
      String text = (String) cell.get("text");

      try {
        String cellType = (String) cell.get("cellType");
        float[] position = PositionUtil.calculateBoundingBox(cell, pdfPage);
        float x = position[0];
        float y = position[1];
        float width = position[2];
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        float fontSize = PositionUtil.calculateBestFitFontSize(cell, pdfPage);
        float charSpace = PositionUtil.calculateCharSpacing(font, text, fontSize, width);
        PdfExtGState textOpacity = new PdfExtGState();
        textOpacity.setFillOpacity(0.0f);

        String role = "TD".equalsIgnoreCase(cellType) ? StandardRoles.TD : StandardRoles.TH;
        TagTreePointer cellPointer = tablePointer.addTag(role);

        // Properly retrieve the tag reference before opening the tag
        TagReference tagRef = cellPointer.getTagReference();
        canvas.openTag(tagRef);

        canvas.beginText()
            .setFontAndSize(font, fontSize)
            .setTextMatrix(x, y)
            .setCharacterSpacing(charSpace)
            .setExtGState(textOpacity)
            .showText(text)
            .endText();

        canvas.closeTag();

        // Move back to the table context
        cellPointer.moveToParent();
      }
      catch (IOException e) {
        logger.error("Error processing table cell", e);
        throw new RuntimeException(e);
      }
    }

    // Move back to the page level after processing all cells
    tablePointer.moveToParent();
  }

}
