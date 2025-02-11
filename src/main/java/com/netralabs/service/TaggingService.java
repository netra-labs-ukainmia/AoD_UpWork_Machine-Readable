package com.netralabs.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.netralabs.processor.PageProcessor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TaggingService {

  private static final Logger logger = LoggerFactory.getLogger(TaggingService.class);

  public void applyTagging(PdfDocument pdfDoc, JSONObject rootNode) {
    pdfDoc.setTagged();
    if (rootNode == null || !rootNode.containsKey("Document")) {
      logger.error("Invalid JSON structure.");
      return;
    }
    new PageProcessor().processPages(pdfDoc, rootNode);
  }

}


