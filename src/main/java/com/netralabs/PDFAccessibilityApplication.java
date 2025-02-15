package com.netralabs;


import com.netralabs.processor.PdfProcessor;
import com.netralabs.service.PDFTextExtractor;
import com.netralabs.utils.ITextTextPositionExtractor;
import com.netralabs.utils.PdfRenderingUtil;
import com.netralabs.utils.PdfUtils;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PDFAccessibilityApplication {

  public static void main(String[] args) throws IOException, ParseException {
    String inputFileName = "files/1-5-24Judgment Book(Hamilton County Superior 4, 6 and 7).pdf";
    String outputSuffix = "_AOD";
    String outputFileName = PdfUtils.appendSuffixToFilename(inputFileName, outputSuffix);
    Map<String, List<float[]>> textPositions = ITextTextPositionExtractor.extractTextPositions(inputFileName);

    PdfProcessor pdfProcessor = new PdfProcessor();
    pdfProcessor.processPDF(inputFileName, outputFileName, textPositions);
  }
}
