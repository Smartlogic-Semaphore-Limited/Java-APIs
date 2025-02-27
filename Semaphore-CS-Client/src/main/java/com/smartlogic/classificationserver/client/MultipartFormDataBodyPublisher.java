package com.smartlogic.classificationserver.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/**
 * Use the MultipartFormDataBodyPublisher instance to construct an HTTP
 * POST body conforming to the content-type "multipart/form-data". Use addPart
 * to add name value pairs. Supports standard string, stream or byte arrays.
 * The body is constructed and serialized into a byte array using UTF-8 character encoding.
 *
 * The caller <em>must</em> set the boundary string in the Content-Type header of the POST request.
 * Fetch the boundary using getBoundary method.
 * Example:
 *
 *  HttpRequest.BodyPublisher bp = HttpRequest.BodyPublishers.ofByteArray(publisher.build());
 *  HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
 *      .version(HttpClient.Version.HTTP_1_1)
 *      .setHeader("Content-Type", "multipart/form-data; boundary=" + publisher.getBoundary())
 *      .uri(new URI(url))
 *      .POST(bp)
 *      ;
 * 
 *
 * @author stevebio
 */
public class MultipartFormDataBodyPublisher {

  protected static final Logger logger = LoggerFactory.getLogger(MultipartFormDataBodyPublisher.class);

  /**
   * The list of PartDef objects. Each PartDef is a name/value pair in a form-data post.
   */
  private final List<BodyPart> partList = new ArrayList<>();

  /**
   * Randomly generated boundary string for the form-data body.
   */
  private final String boundary = UUID.randomUUID().toString();

  /**
   * Build the body contents per multipart/form-data and serialize into
   * byte array using encoding UTF-8.
   * @return the byte array with the HTTP POST body conforming to multipart/form-data.
   */
  public byte[] build() {
    if (partList.isEmpty()) {
      throw new IllegalStateException("Must have at least one part to build multipart message.");
    }
    addFinalBoundaryPart();

    /* Serialize the entire body into a byte array using UTF-8 encoding and return. */
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    new BodyPartsIterator().forEachRemaining(partsSpecification -> {
      try {
        baos.write(partsSpecification);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    });
    if (logger.isDebugEnabled()) {
      logger.debug("multipart/form-data body: {}", baos.toString(StandardCharsets.UTF_8));
    }
    return baos.toByteArray();
  }

  /**
   * Return the boundary string used for the multipart/form-data body.
   * @return the boundary string
   */
  public String getBoundary() {
    return boundary;
  }

  /**
   * Add a name-value pair where the value is a String.
   * @param name the param name
   * @param value the param value
   * @return this object (for fluent/chaining)
   */
  public MultipartFormDataBodyPublisher addPart(String name, String value) {
    BodyPart newPart = new BodyPart();
    newPart.type = BodyPart.TYPE.STRING;
    newPart.name = name;
    newPart.value = value;
    partList.add(newPart);
    return this;
  }

  /**
   * Add a part using the specified file path as the value.
   * @param name the param name
   * @param value the path to the file with the value.
   * @return this object (fluent/chaining)
   */
  public MultipartFormDataBodyPublisher addPart(String name, Path value) {
    BodyPart newPart = new BodyPart();
    newPart.type = BodyPart.TYPE.FILE;
    newPart.name = name;
    newPart.path = value;
    partList.add(newPart);
    return this;
  }

  /**
   * Add a part with a value that is from an input stream supplier.
   * @param name the param name
   * @param value the InputStream supplier
   * @param filename the name of the file (if applicable)
   * @param contentType the type of content.
   * @return this object (fluent/chaining)
   */
  public MultipartFormDataBodyPublisher addPart(String name, Supplier<InputStream> value, String filename, String contentType) {
    BodyPart newPart = new BodyPart();
    newPart.type = BodyPart.TYPE.STREAM;
    newPart.name = name;
    newPart.stream = value;
    newPart.filename = filename;
    newPart.contentType = contentType;
    partList.add(newPart);
    return this;
  }

  /**
   * Adds the final boundary marker at the end of the body to indicate the body is complete.
   */
  private void addFinalBoundaryPart() {
    if (partList.get(partList.size() - 1).type != BodyPart.TYPE.FINAL_BOUNDARY) {
      BodyPart newPart = new BodyPart();
      newPart.type = BodyPart.TYPE.FINAL_BOUNDARY;
      newPart.value = "--" + boundary + "--";
      partList.add(newPart);
    }
  }

  /**
   * An object representing a logica part of a multipart/form-data request body.
   */
  static class BodyPart {

    public enum TYPE {
      STRING, FILE, STREAM, FINAL_BOUNDARY
    }

    BodyPart.TYPE type;
    String name;
    String value;
    Path path;
    Supplier<InputStream> stream;
    String filename;
    String contentType;

  }

  /**
   * Iterator on the parts of the body. Used to build the serialized output.
   */
  class BodyPartsIterator implements Iterator<byte[]> {

    private Iterator<BodyPart> iter;
    private InputStream currentFileInput;

    private boolean done;
    private byte[] next;

    BodyPartsIterator() {
      iter = partList.iterator();
    }

    @Override
    public boolean hasNext() {
      if (done) return false;
      if (next != null) return true;
      try {
        next = computeNext();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
      if (next == null) {
        done = true;
        return false;
      }
      return true;
    }

    @Override
    public byte[] next() {
      if (!hasNext()) throw new NoSuchElementException();
      byte[] res = next;
      next = null;
      return res;
    }

    private byte[] computeNext() throws IOException {
      if (currentFileInput == null) {
        if (!iter.hasNext()) return null;
        BodyPart nextPart = iter.next();
        if (BodyPart.TYPE.STRING.equals(nextPart.type)) {
          String part =
              "--" + boundary + "\r\n" +
                  "Content-Disposition: form-data; name=\"" + nextPart.name + "\"\r\n\r\n" +
                  nextPart.value + "\r\n";
          return part.getBytes(StandardCharsets.UTF_8);
        }
        if (BodyPart.TYPE.FINAL_BOUNDARY.equals(nextPart.type)) {
          return nextPart.value.getBytes(StandardCharsets.UTF_8);
        }
        String filename;
        String contentType;
        if (BodyPart.TYPE.FILE.equals(nextPart.type)) {
          Path path = nextPart.path;
          filename = path.getFileName().toString();
          contentType = Files.probeContentType(path);
          if (contentType == null) contentType = "application/octet-stream";
          currentFileInput = Files.newInputStream(path);
        } else {
          filename = nextPart.filename;
          contentType = nextPart.contentType;
          if (contentType == null) contentType = "application/octet-stream";
          currentFileInput = nextPart.stream.get();
        }
        String partHeader =
            "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + nextPart.name + "\"; filename=\"" + filename + "\"\r\n" +
                "Content-Type: " + contentType + "\r\n\r\n";
        return partHeader.getBytes(StandardCharsets.UTF_8);
      } else {
        byte[] buf = new byte[8192];
        int r = currentFileInput.read(buf);
        if (r > 0) {
          byte[] actualBytes = new byte[r];
          System.arraycopy(buf, 0, actualBytes, 0, r);
          return actualBytes;
        } else {
          currentFileInput.close();
          currentFileInput = null;
          return "\r\n".getBytes(StandardCharsets.UTF_8);
        }
      }
    }
  }
}