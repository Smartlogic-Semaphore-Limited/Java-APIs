@Library('smartlogic-common@v2') _

this.dockerUtils = smartlogic.api('Docker')
this.workbenchContainer = null
this.workbenchPort = null

smartlogic([
  docker: "maven:3.6.3-openjdk-17",
  builder: smartlogic.mavenBuilder(args: {["-Dgpg.useagent=true -P integration -DOE_BASE_URL=http://${env.NODE_NAME}:${this.workbenchPort}/"]}, credentialIds: ["MavenCentral"]),
  beforeBuild: {
    dockerUtils.withRegistry() {
      def image = docker.image("sl-cart01:8082/semaphore-kmm:master")
      image.pull()
      this.workbenchContainer = dockerUtils.run(image, [containerArgs: "-v ${env.SEMAPHORE_LICENCE_DIR}:/var/opt/semaphore/studio/data/licenses", runArgs: "-v ${env.SEMAPHORE_LICENCE_DIR}:/var/opt/semaphore/studio/data/licenses", privileges: true])
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
  settings: [
    polaris: [scan: [buildTool: "mvn"]],
    includeSemaphoreLicense: 'valid_licence_unlimited',
  ]
])
