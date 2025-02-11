package com.netralabs.utils;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;

public class TaggingUtil {

  public static String getStandardRoleByName(String name) {
    if (name == null) return StandardRoles.SPAN; // Default to a generic role
    switch (name.toUpperCase()) {
      case "DOCUMENT": return StandardRoles.DOCUMENT;
      case "SECT": return StandardRoles.SECT;
      case "H1": return StandardRoles.H1;
      case "H2": return StandardRoles.H2;
      case "H3": return StandardRoles.H3;
      case "H4": return StandardRoles.H4;
      case "H5": return StandardRoles.H5;
      case "H6": return StandardRoles.H6;
      case "P": return StandardRoles.P;
      case "TABLE": return StandardRoles.TABLE;
      case "TR": return StandardRoles.TR;
      case "TD": return StandardRoles.TD;
      case "TH": return StandardRoles.TH;
      case "LIST": return StandardRoles.L;
      case "LI": return StandardRoles.LI;
      case "FIGURE": return StandardRoles.FIGURE;
      case "CAPTION": return StandardRoles.CAPTION;
      case "LINK": return StandardRoles.LINK;
      case "FORM": return StandardRoles.FORM;
      case "NOTE": return StandardRoles.NOTE;
      case "QUOTE": return StandardRoles.BLOCKQUOTE;
      default:
        System.err.println("⚠ Unknown Standard Role: " + name);
        return StandardRoles.SPAN;
    }
  }

}
