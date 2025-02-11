package com.netralabs.processor;

import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import org.json.simple.JSONObject;

public interface TagProcessorInterface  {


  void process(TagTreePointer pagePointer, JSONObject tagObj, PdfPage pdfPage);


}
