@Library('smartlogic-common@v2') _

this.dockerUtils = smartlogic.api('Docker')

smartlogic([
  docker: "maven:3.6.3-openjdk-17",
  builder: smartlogic.mavenBuilder(args: {["-Dgpg.useagent=true -P integration"]}, credentialIds: ["MavenCentral"]),
  buildWrapper: {
    withCredentials([file(credentialsId: 'gpgsecring', variable: 'GPG_FILE')]) {
      sh "gpg --import " + env.GPG_FILE
    }
    dockerUtils.withRegistry() {
     def image = docker.image("sl-cart01:8082/semaphore-kmm:master")
     image.pull()
     dockerUtils.withRun(image, [containerArgs: getRunArgs(), privileges: true]) { container ->
       it()
     }
    }
  },
  settings: [
    polaris: [scan: [buildTool: "mvn"]]
  ]
])
