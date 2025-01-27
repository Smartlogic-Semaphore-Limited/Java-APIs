@Library('smartlogic-common@v2') _

smartlogic([
  docker: "maven:3.6.3-openjdk-17",
  builder: smartlogic.mavenBuilder(args: {["-Dgpg.useagent=true"]}, credentialIds: ["MavenCentral"]),
  buildWrapper: {
    withCredentials([file(credentialsId: 'gpgsecring', variable: 'GPG_FILE')]) {
      sh "gpg --import " + env.GPG_FILE
    }
    it()
  }
])
