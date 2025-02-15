package com.netralabs.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PDFTextExtractor {

  public static Map<String, List<float[]>> extractTextPositions(String filePath) throws IOException {
    Map<String, List<float[]>> textPositions = new LinkedHashMap<>();
    try (PDDocument document = PDDocument.load(new File(filePath))) {
      PDFTextStripper stripper = new PDFTextStripper() {
        @Override
        protected void writeString(String text, List<TextPosition> textPositionsList)  {
          List<float[]> positions = new ArrayList<>();
          for (TextPosition textPosition : textPositionsList) {
            positions.add(new float[] {
                textPosition.getXDirAdj(),
                textPosition.getYDirAdj(),
                textPosition.getWidthDirAdj(),
                textPosition.getHeightDir()
             });
          }
          textPositions.put(text, positions);
        }
      };
      stripper.setSortByPosition(true);
      stripper.getText(document);
    }

    return textPositions;
  }

}
