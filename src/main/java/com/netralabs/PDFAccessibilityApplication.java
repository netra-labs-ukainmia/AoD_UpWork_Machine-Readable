package com.netralabs;


import com.netralabs.processor.PdfProcessor;
import com.netralabs.utils.PdfUtils;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class PDFAccessibilityApplication {

  public static void main(String[] args) throws IOException, ParseException {
    String inputFileName = "files/1-5-24Judgment Book(Hamilton County Superior 4, 6 and 7).pdf";
    String outputSuffix = "_AOD";
    String outputFileName = PdfUtils.appendSuffixToFilename(inputFileName, outputSuffix);

    PdfProcessor pdfProcessor = new PdfProcessor();
    pdfProcessor.processPDF(inputFileName, outputFileName);
  }
}
