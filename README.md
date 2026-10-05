# Semaphore Java APIs

Java connectors to Semaphore services, built as a multi-module Maven project.

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

The task branch temporarily loads `smartlogic-common@ta39232-blackduck-snapshot-settings`
to demonstrate the shared-library change. Restore `smartlogic-common@v2` once
that change is available through the production library reference.
