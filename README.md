# Semaphore Java APIs

Java connectors to Semaphore services, built as a multi-module Maven project.

## Release 5.13.0 compliance files

Approved files exported on 2026-10-19 are stored unchanged in this repository:

| File | Original export |
| --- | --- |
| [Legal notices](release/5.13.0/legal-notices.txt) | Legal Notice File for Progress Semaphore APIs for Java - 5.13.0 - 2026-10-19.txt |
| [SPDX 2.3 ZIP](release/5.13.0/spdx-2.3.zip) | Java_APIs-release-5.13.0_SPDX_23_2026-10-19_154643.zip |
| [SPDX 3.0 ZIP](release/5.13.0/spdx-3.0.zip) | Java_APIs-release-5.13.0_SPDX_30_2026-10-19_154718.zip |

Maven attaches these files during `package` to the parent artifact
`com.smartlogic:Semaphore-Java-APIs`, not to each child module. Normal `install`
and release signing/deployment include the attached files. The existing Apache
License 2.0 declaration is unchanged.

After **5.13.0 is published to Maven Central**, the downloads will be:

- [Legal notices](https://repo.maven.apache.org/maven2/com/smartlogic/Semaphore-Java-APIs/5.13.0/Semaphore-Java-APIs-5.13.0-legal-notices.txt)
- [SPDX 2.3 ZIP](https://repo.maven.apache.org/maven2/com/smartlogic/Semaphore-Java-APIs/5.13.0/Semaphore-Java-APIs-5.13.0-spdx-2.3.zip)
- [SPDX 3.0 ZIP](https://repo.maven.apache.org/maven2/com/smartlogic/Semaphore-Java-APIs/5.13.0/Semaphore-Java-APIs-5.13.0-spdx-3.0.zip)

The same URLs are exposed in the parent POM properties
`release.legal-notices.url`, `release.spdx23.url`, and `release.spdx30.url`.
Attachments are separate downloads: depending on a module does not download them
automatically or embed them inside its JAR.

Both `5.13.0-SNAPSHOT` development builds and the final `5.13.0` release use
these files. A version guard fails validation for other project versions rather
than publishing stale release documents. Before another release, obtain approved
files for that version, update `release.files.version` and these links, and add
the files under the matching `release/<version>/` directory. Git attributes
disable newline conversion under `release/` to preserve the supplied bytes.

## Black Duck scanning

The Jenkins pipeline runs Black Duck before the build when `RUN_BLACKDUCK_SCAN`
is enabled. The scan's `enableMavenSnapshots` option enables snapshot resolution
in the Jenkins-generated Maven settings, not in the project POM.

These settings enable snapshot downloads for dependency extraction. Some modules
have test dependencies on sibling snapshot JARs; the scan's standalone Maven
invocation must resolve these even though test dependencies are excluded from
the resulting scan. A repository mirror alone does not enable snapshot downloads.

The repository URL and snapshot profile exist only in temporary scan settings,
not in the published POM. Normal builds are unchanged. Scanning neither installs
reactor artifacts nor publishes them, and retains the shared pipeline's Maven
mirror and Black Duck accuracy requirement.
