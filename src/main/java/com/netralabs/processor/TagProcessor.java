package com.netralabs.processor;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.netralabs.processor.impl.FigureProcessor;
import com.netralabs.processor.impl.ListProcessor;
import com.netralabs.processor.impl.TableProcessor;
import com.netralabs.processor.impl.TextProcessor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TagProcessor {

  private final Map<String, TagProcessorInterface> processors;
  private static final Logger logger = LoggerFactory.getLogger(TagProcessor.class);


  public TagProcessor() {
    processors = new HashMap<>();
    processors.put("Table", new TableProcessor());
    processors.put("TH", new TableProcessor());
    processors.put("TD", new TableProcessor());
    processors.put("Figure", new FigureProcessor());
    processors.put("H1", new TextProcessor());
    processors.put("H2", new TextProcessor());
    processors.put("H3", new TextProcessor());
    processors.put("H4", new TextProcessor());
    processors.put("H5", new TextProcessor());
    processors.put("H6", new TextProcessor());
    processors.put("H7", new TextProcessor());
    processors.put("P", new TextProcessor());
    processors.put("link", new TextProcessor());
    processors.put("Form", new TextProcessor());
    processors.put("Note", new TextProcessor());
    processors.put("Caption", new TextProcessor());
    processors.put("Quote", new TextProcessor());
    processors.put("List", new ListProcessor());
    processors.put("LI", new ListProcessor());
  }

  public void processTagsObjects(TagTreePointer pagePointer, JSONObject pageObj, PdfPage pdfPage) {
    if (!pageObj.containsKey("TagObjects")) {
      logger.warn("Invalid JSON structure: Missing 'TagObjects'.");
      return;
    }

    JSONArray jsonObjects = (JSONArray) pageObj.get("TagObjects");
    for (Object tagObj : jsonObjects) {
      JSONObject tag = (JSONObject) tagObj;
      String nameTag = (String) tag.get("tag");
      logger.info("Processing tag: {}", nameTag);

      TagProcessorInterface processor = processors.getOrDefault(nameTag, new TextProcessor());
      if (processor instanceof TextProcessor) {
        logger.warn("No specific processor found for '{}'. Using default TextProcessor.", nameTag);
      }
      processor.process(pagePointer, tag, pdfPage);
    }
  }


}
