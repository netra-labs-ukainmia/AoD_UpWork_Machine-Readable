package com.netralabs.processor;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PageProcessor {

  public void processPages(PdfDocument pdfDoc, JSONObject rootNode) {
    JSONObject docNode = (JSONObject) rootNode.get("Document");
    if (!docNode.containsKey("Pages")) {
      System.out.println("Invalid JSON structure.");
      return;
    }
    JSONArray pages = (JSONArray) docNode.get("Pages");

    for (Object pageObj : pages) {
      JSONObject pageJson = (JSONObject) pageObj;
      int pageNumber = ((Long) pageJson.get("Page")).intValue();

      PdfPage page;
      if (pageNumber <= pdfDoc.getNumberOfPages()) {
        page = pdfDoc.getPage(pageNumber);
      } else {
        float pWidth = ((Number) pageJson.get("PageWidth")).floatValue();
        float pHeight = ((Number) pageJson.get("PageHeight")).floatValue();
        page = pdfDoc.addNewPage(new PageSize(pWidth, pHeight));
      }

      TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
      tagPointer.moveToRoot();
      TagTreePointer pagePointer = tagPointer.addTag(StandardRoles.SECT);
      pagePointer.setPageForTagging(page);

      new TagProcessor().processTagsObjects(pagePointer, pageJson, page);
    }
  }

}
