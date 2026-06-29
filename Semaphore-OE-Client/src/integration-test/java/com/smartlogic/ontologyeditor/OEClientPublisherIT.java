// Copyright (c) 2026 Progress Software Corporation and/or its subsidiaries or affiliates. All rights reserved.
package com.smartlogic.ontologyeditor;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for publisher configuration operations.
 * Tests uploading publisher configurations.
 */
public class OEClientPublisherIT extends AbstractModelScopedIT {

  @Test
  public void uploadPublisherConfiguration() throws OEClientException, IOException {
    byte[] zipData = createMinimalZipFile();

    oeClient.uploadPublisherConfiguration(zipData);

    assertNotNull("Publisher configuration should be uploaded", zipData);
  }

  @Test
  public void downloadAndUploadPublisherConfiguration() throws OEClientException {
    byte[] downloadedConfig = oeClient.downloadPublisherConfiguration();

    assertNotNull("Publisher configuration should be downloaded", downloadedConfig);
    // Re-upload the same configuration
    oeClient.uploadPublisherConfiguration(downloadedConfig);
  }

  private byte[] createMinimalZipFile() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipOutputStream zos = new ZipOutputStream(baos);

    // Add a minimal valid entry
    ZipEntry entry = new ZipEntry("config.xml");
    zos.putNextEntry(entry);
    zos.write("<config/>".getBytes());
    zos.closeEntry();

    zos.close();
    return baos.toByteArray();
  }
}

