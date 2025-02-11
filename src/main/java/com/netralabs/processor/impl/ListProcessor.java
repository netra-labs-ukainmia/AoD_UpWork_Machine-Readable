package com.netralabs.processor.impl;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.netralabs.processor.TagProcessorInterface;
import com.netralabs.utils.PdfRenderingUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListProcessor implements TagProcessorInterface {

  private static final Logger logger = LoggerFactory.getLogger(ListProcessor.class);

  @Override
  public void process(TagTreePointer pagePointer, JSONObject tagObj, PdfPage pdfPage) {
    TagTreePointer listPointer = PdfRenderingUtil.createTag(pagePointer, StandardRoles.L, pdfPage);

    JSONArray bullets = (JSONArray) tagObj.get("bullets");
    if (bullets == null || bullets.isEmpty()) {
      logger.warn("Skipping List: No list items found.");
      return;
    }

    for (Object bulletObj : bullets) {
      JSONObject bullet = (JSONObject) bulletObj;
      TagTreePointer itemPointer = PdfRenderingUtil.createTag(listPointer, StandardRoles.LI, pdfPage);
      PdfRenderingUtil.renderText(itemPointer, bullet, pdfPage);
      itemPointer.moveToParent();
    }

    listPointer.moveToParent();
  }

}
