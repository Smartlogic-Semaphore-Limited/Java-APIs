@Library('smartlogic-common@v2') _

smartlogic([
  docker: "maven:3.8.3-jdk-8",
  builder: smartlogic.mavenBuilder(args: {["-Dgpg.useagent=true"]}, credentialIds: ["MavenCentral"]),
  buildWrapper: {
    withCredentials([file(credentialsId: 'gpgsecring', variable: 'GPG_FILE')]) {
      sh "gpg --import " + env.GPG_FILE
    }
    it()
  },
  settings: [
    polaris: [scan: [buildTool: "mvn"]]
  ]
])
