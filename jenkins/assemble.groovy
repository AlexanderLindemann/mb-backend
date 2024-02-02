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
        choice(name: 'DOCKER_REPOSITORY_NAME', choices: ["mb-docker"], description: 'Docker repository for backend apps')
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
        stage("Unit test") {
            steps {
                script {
                    boolean isContainerRunning = true
                    int attempts = 0
                    while (isContainerRunning && attempts < 10) {
                        attempts++
                        String existingContainer = sh(script: 'docker ps -aqf "name=^mb-backend-db-test$"', returnStdout: true).trim()
                        if (!existingContainer) {
                            isContainerRunning = false
                        } else {
                            echo "Ожидание освобождения контейнера mb-backend-db-test (попытка ${attempts})"
                            sleep(30)
                        }
                    }
                    try {
                        sh "docker run -d -p 40002:5432 --name mb-backend-db-test mb-docker.practus.ru/mb-postgres:15.4"
                        executeGradlewCommand('test')
                    } finally {
                        try {
                            sh "docker stop mb-backend-db-test"
                            sh "docker rm mb-backend-db-test"
                        } catch (Exception e) {
                            echo "Ошибка при удалении Docker контейнера 'mb-backend-db-test': ${e.message}"
                        }
                    }
                }
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
                    String imageVersion = env.BUILD_VERSION
                    imageVersion = imageVersion.replace('/', '_')
                    String dockerRepositoryUrl = nexus_repository.getDockerRepositoryUrl(params.DOCKER_REPOSITORY_NAME)
                    executeGradlewCommand("dockerImagePushDetailed -Pbuild.version=${imageVersion} -Pdocker.repository.url=${dockerRepositoryUrl}")
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
