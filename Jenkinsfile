pipeline {
	agent any
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
				sh '''
					docker build -t alunwcom/moany-public:${BUILD_ID} -t alunwcom/moany-public:latest -f Dockerfile .
				'''
			}
		}
		stage('deploy-snapshot') {
			when {
				not {
					tag 'v*.*.*'
				}
			}
			steps {
				sh '''
				    docker rm -f moany-app-uat || true
				    docker rm -f moany-db-uat || true
				    docker network prune -f
				    docker network create moany || true
				    docker run -d -p 9080:9080 --network="moany" --env-file h2.env --name moany-app-uat alunwcom/moany-public:${BUILD_ID}
				'''
			}
		}
		stage('publish-artifacts') {
			steps {
				sh '''
					docker image ls | grep moany-public
				'''
			}
		}
	}
}

