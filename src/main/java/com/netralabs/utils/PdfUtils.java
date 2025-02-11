package com.netralabs.utils;

import java.io.File;

public class PdfUtils {
  public static String appendSuffixToFilename(String fullPath, String suffix) {
    int lastDotIndex = fullPath.lastIndexOf('.');
    int lastSeparatorIndex = fullPath.lastIndexOf(File.separator);
    if (lastDotIndex > lastSeparatorIndex) {
      return fullPath.substring(0, lastDotIndex) + suffix + fullPath.substring(lastDotIndex);
    } else {
      return fullPath + suffix;
    }
  }

}
