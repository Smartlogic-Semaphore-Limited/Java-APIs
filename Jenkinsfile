@Library('smartlogic-common@v2') _

this.dockerUtils = smartlogic.api('Docker')
this.workbenchContainer = null
this.workbenchPort = null

smartlogic([
  docker: "maven@sha256:7f46feaf907771cd14e38d0b901d6372e50e68bf4e2197b0f181eb16f051081b", //   maven:3.6.3-openjdk-17
  builder: smartlogic.mavenBuilder(args: { getMvnArgs() }, credentialIds: ["MavenCentral"]),
  beforeBuild: {
    dockerUtils.withRegistry() {
      def image = docker.image("sl-cart01:8082/semaphore-kmm:${params.KMM_IMAGE_TAG}")
      image.pull()
      def containerArgs = "-v ${env.SEMAPHORE_LICENCE_DIR}:/var/opt/semaphore/studio/data/licenses"
      this.workbenchContainer = dockerUtils.run(image, [containerArgs: containerArgs, privileges: true])
      this.workbenchPort = dockerUtils.getPort(this.workbenchContainer, 5082)
    }
  },
  afterBuild: {
    if (this.workbenchContainer != null) {
      this.workbenchContainer.stop()
    }
  },
  buildWrapper: {
    withCredentials([file(credentialsId: 'gpgsecring', variable: 'GPG_FILE')]) {
      sh "gpg --import " + env.GPG_FILE
    }
    it()
  },
  parameters: [
    string(name: 'KMM_IMAGE_TAG', defaultValue: "master", description: 'The tag of the KMM image to use'),
  ],
  settings: [
    polaris: [scan: [buildTool: "mvn"]],
    includeSemaphoreLicense: 'valid_licence_unlimited',
  ]
])

def getMvnArgs() {
    [
        "-Dgpg.useagent=true",
        "-P integration",
        "-DOE_BASE_URL=http://${env.NODE_NAME}:${this.workbenchPort}/"
    ]
}
