package com.smartlogic.oebatch.beans;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A record class that holds information about a failed KMM job request.
 * @param jobId the job id
 * @param httpStatusCode the http status code
 * @param errors the list of KMMError objects
 */
public record JobResult(String jobId, Integer httpStatusCode, List<KMMError> errors) {
  public JobResult {
    checkNotNull(jobId);
    checkNotNull(httpStatusCode);
    checkNotNull(errors);
  }
}
