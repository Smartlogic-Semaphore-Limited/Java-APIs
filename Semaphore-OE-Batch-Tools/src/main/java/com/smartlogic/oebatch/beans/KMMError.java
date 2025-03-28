package com.smartlogic.oebatch.beans;

import jakarta.json.JsonObject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Record containing details of a KMM error.
 * @param errorType the error type
 * @param params the parameters of the error
 */
public record KMMError(String errorType, KMMErrorParams params) {
  public KMMError {
    checkNotNull(errorType);
    checkNotNull(params);
  }

  /**
   * Special constructor using corresponding JSON object.
   * @param jsonObject the JSON object
   */
  public KMMError(JsonObject jsonObject) {
    this(jsonObject.getString("type"), new KMMErrorParams(jsonObject.get("params").asJsonObject()));
  }
}
