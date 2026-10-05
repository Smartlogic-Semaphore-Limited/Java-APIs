# Semaphore Java APIs

Java connectors to Semaphore services, built as a multi-module Maven project.

## Black Duck scanning

The Jenkins pipeline runs Black Duck after the build when `RUN_BLACKDUCK_SCAN`
is enabled. Its scan container sets `blackduck.snapshot.repository.url` through
`MAVEN_OPTS`, activating the `blackduck-snapshot-resolution` Maven profile.

This profile enables snapshot downloads for dependency extraction. Some modules
have test dependencies on sibling snapshot JARs; the scan's standalone Maven
invocation must resolve these even though test dependencies are excluded from
the resulting scan. A repository mirror alone does not enable snapshot downloads.

The repository URL is supplied by Jenkins, not embedded in the published POM.
Normal builds leave this profile inactive. Scanning neither installs reactor
artifacts nor publishes them, and retains the shared pipeline's Maven settings
and Black Duck accuracy requirement.
