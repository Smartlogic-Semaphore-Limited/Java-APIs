@Library('smartlogic-common@v2') _

this.dockerUtils = smartlogic.api('Docker')
this.workbenchContainer

smartlogic([
  docker: "maven:3.6.3-openjdk-17",
  builder: smartlogic.mavenBuilder(args: {["-Dgpg.useagent=true -P integration"]}, credentialIds: ["MavenCentral"]),
  beforeBuild: {
    dockerUtils.withRegistry() {
      def image = docker.image("sl-cart01:8082/semaphore-kmm:master")
      image.pull()
      this.workbenchContainer = dockerUtils.run(image, [containerArgs: getRunArgs(), privileges: true])
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
  },
  settings: [
    polaris: [scan: [buildTool: "mvn"]]
  ]
])
