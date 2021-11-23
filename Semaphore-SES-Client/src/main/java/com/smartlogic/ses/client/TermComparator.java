// ----------------------------------------------------------------------
// Product: Semantic Enhancement Server Java API
//
// (c) 2009 Smartlogic Semaphore Ltd
// ----------------------------------------------------------------------
package com.smartlogic.ses.client;

import java.io.Serializable;
import java.util.Comparator;

public class TermComparator implements Comparator<Term>, Serializable {
  private static final long serialVersionUID = 99028847731901618L;

  @Override
  public int compare(Term term0, Term term1) {
    if (term0 == null)
      return 1;
    if (term1 == null)
      return -1;

    if (term0.getName() == null)
      return 1;
    if (term1.getName() == null)
      return -1;

    if (term0.getName().getValue() == null)
      return 1;
    if (term1.getName().getValue() == null)
      return -1;

    return term0.getName().getValue().compareToIgnoreCase(term1.getName().getValue());
  }

}
