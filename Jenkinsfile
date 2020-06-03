
// Jenkins declarative pipeline

pipeline {
	agent {
		dockerfile {
			//filename 'Dockerfile'
			//label 'moany-public'
			additionalBuildArgs  ' -t alunwcom/moany-public '
			args ' --entrypoint /bin/sh'
		}
	}
	triggers {
		pollSCM('H/5 * * * *')
	}
	options {
		buildDiscarder(logRotator(numToKeepStr: '7'))
	}
	stages {
		stage('init') {
			steps {
				echo "Using workspace [${WORKSPACE}]"
				deleteDir() // clear workspace
			}
		}
		stage('build-snapshot') {
			when {
				not {
					tag 'v*.*.*'
				}
			}
			steps {
				echo "Git commit = ${GIT_COMMIT}"
				sh 'java -version'
			}
		}
		stage('publish-artifacts') {
			steps {
				echo "TODO"
			}
		}
	}
}

