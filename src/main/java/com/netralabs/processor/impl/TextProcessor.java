package com.netralabs.processor.impl;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.tagutils.TagReference;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.netralabs.processor.TagProcessorInterface;
import com.netralabs.utils.PdfRenderingUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextProcessor implements TagProcessorInterface {

  private static final Logger logger = LoggerFactory.getLogger(TextProcessor.class);

  @Override
  public void process(TagTreePointer pagePointer, JSONObject tagObj, PdfPage pdfPage) {
    String tagName = (String) tagObj.get("tag");
    processTextTags(pagePointer, tagName, pdfPage, tagObj);
  }

  public void processTextTags(TagTreePointer pagePointer, String nameTag, PdfPage pdfPage, JSONObject tagObj) {
    TagTreePointer tagPointer = pagePointer.addTag(nameTag);
    tagPointer.setPageForTagging(pdfPage);
    processLines(tagPointer, nameTag, tagObj, pdfPage);
    tagPointer.moveToParent();
  }

  private void processLines(TagTreePointer tagPointer, String nameTag, JSONObject tagObj, PdfPage pdfPage) {
    JSONArray lines = (JSONArray) tagObj.get("lines");
    if (lines == null || lines.isEmpty()) {
      logger.warn("Skipping: No lines found in '{}'.", tagObj.get("tag"));
      return;
    }

    if (nameTag.equals("P") && lines.stream().toList().size() > 1) {
      processParagraph(tagPointer, tagObj, pdfPage);
    } else {
      for (Object lineObj : lines) {
        processTextLines(tagPointer, (JSONObject) lineObj, pdfPage);
      }
    }
  }


  private void processParagraph(TagTreePointer tagPointer, JSONObject tagObj, PdfPage pdfPage) {
    try {
      TagReference tagRef = tagPointer.getTagReference();
      PdfRenderingUtil.renderParagraph(tagRef, tagObj, pdfPage);
    } catch (Exception e) {
      logger.error("Error processing paragraph: ", e);
    }
  }

  private void processTextLines(TagTreePointer cellPointer, JSONObject cellObj, PdfPage pdfPage) {
    PdfRenderingUtil.renderText(cellPointer, cellObj, pdfPage);
  }
}