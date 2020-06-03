pipeline {
	agent {
		dockerfile {
			filename 'Dockerfile.build'
			additionalBuildArgs  ' -t alunwcom/moany-public-build '
			args '-v workspace:/workspace'
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
				sh 'ls -l workspace'
			}
		}
		stage('publish-artifacts') {
			steps {
				echo "TODO"
			}
		}
	}
}

