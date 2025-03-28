package com.smartlogic.oebatch.beans;

import jakarta.json.JsonObject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Record to hold params fields for KMM error
 * @param errorLevel the error level
 * @param constraintId the constraint id
 * @param message the message
 * @param root the root (cause, URI)
 */
public record KMMErrorParams(String errorLevel, String constraintId, String message, String root) {
  public KMMErrorParams {
    checkNotNull(errorLevel);
    checkNotNull(constraintId);
  }

  /**
   * Special constructor using corresponding JSON object.
   * @param jsonObject the JSON object
   */
  public KMMErrorParams(JsonObject jsonObject) {
    this(
        jsonObject.getString("level"),
        jsonObject.getString("constraintId"),
        jsonObject.getString("message"),
        jsonObject.getString("root"));
  }
}
