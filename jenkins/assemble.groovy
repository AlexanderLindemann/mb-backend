@Library('sd-jenkins-library') _

import com.smartdeal.jenkins.Constants

pipeline {
    agent any
    tools {
        jdk 'jdk-11'
    }
    options {
        buildDiscarder(logRotator(numToKeepStr: Constants.LogRotation.NUM_TO_KEEP))
        timeout(time: Constants.Dev.Backend.TIMEOUT_TIME, unit: Constants.Dev.Backend.TIMEOUT_UNIT)
    }
    parameters {
        gitParameter(
                name: 'GIT_BRANCH',
                useRepository: "https://git.practus.ru/scm/mb/mb-backend.git",
                defaultValue: "origin/develop",
                type: 'PT_BRANCH',
                description: 'Branch for build'
        )
        string(name: 'GIT_COMMIT_ID', description: 'ID of Git commit for build', defaultValue: 'HEAD')
        choice(name: 'DOCKER_REPOSITORY_NAME', choices: ["practus-docker-snapshot", "practus-docker"], description: 'Docker repository for backend apps')
        string(name: "EXTERNAL_BUILD_VERSION", description: 'If we need to assemble specific')
    }
    environment {
        GIT_BRANCH = fixOriginPrefix(params.GIT_BRANCH)
        BUILD_DATE_PART = getDateVersionPart()
        BUILD_VERSION_SHORT = getBuildVersion(env.GIT_BRANCH, env.BUILD_DATE_PART)
//        SLACK_NOTIFICATION_CHANNEL = "sd-jenkins-assemble"
    }
    stages {
        stage("Init") {
            steps {
                script {
                    setBuildName(env.BUILD_VERSION_SHORT)
                    setBackendReleaseAndVersions(env.GIT_BRANCH)
                    bitbucketProcessTracker.started(params.GIT_COMMIT_ID, "assemble", "Assembling")
                }
            }
        }
        stage("Use input build coordinates") {
            when {
                expression {
                    params.EXTERNAL_BUILD_VERSION
                }
            }
            steps {
                script {
                    env.BUILD_VERSION = params.EXTERNAL_BUILD_VERSION
                    setBuildName(env.BUILD_VERSION)
                    sh "git checkout ${params.GIT_COMMIT_ID}"
                    property_utils.updatePropertyValue('gradle.properties', 'project_version', env.BUILD_VERSION)
                }
            }
        }
        stage("Build") {
            steps {
                executeGradlewCommand('assemble')
            }
        }
        stage("Build Docker") {
            steps {
                executeGradlewCommand('buildDocker')
            }
        }
        stage("Publish") {
            steps {
                executeGradlewCommand('publish')
            }
        }
        stage("Push docker") {
            steps {
                script {
                    String dockerRepositoryUrl = nexus_repository.getDockerRepositoryUrl(params.DOCKER_REPOSITORY_NAME)
                    executeGradlewCommand("dockerImagePushDetailed -Pbuild.version=${env.BUILD_VERSION} -Pdocker.repository.url=${dockerRepositoryUrl}")
                }
            }
        }
    }
    post {
        always {
            script {
                bitbucketProcessTracker.finished(params.GIT_COMMIT_ID, "assemble", "Assembled ${env.BUILD_VERSION_SHORT}")
            }
        }
        success {
            cleanWorkspace()
        }
        /*unstable {
            notifyBuildUnstable()
        }
        failure {
            notifyBuildFailed()
        }*/
    }
}
