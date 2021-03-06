package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingPullRequestException
import xyz.ronella.gradle.plugin.simple.git.exception.MissingRemoteException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitFetchPRTest {

    private Project project;

    @BeforeEach
    public void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-git'
        project.extensions.simple_git.verbose = true
        project.extensions.simple_git.noop = true
    }

    @Test
    public void testNoParameters() {
        def gitTask = project.tasks.gitFetchPR

        assertThrows(MissingRemoteException, {
            gitTask.executeCommand()
        })
    }

    @Test
    public void testRemote() {
        def gitTask = project.tasks.gitFetchPR

        gitTask.remote = "origin"

        assertThrows(MissingPullRequestException, {
            gitTask.executeCommand()
        })
    }

    @Test
    public void testPullRequest() {
        def gitTask = project.tasks.gitFetchPR

        def pullRequest = 1
        gitTask.remote = "origin"
        gitTask.pullRequest = pullRequest

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} fetch \"origin\" pull/${pullRequest}/head:pr-${pullRequest}".toString(), cmd)
    }

    @Test
    public void testPullRequestZargs() {
        def gitTask = project.tasks.gitFetchPR

        def pullRequest = 1
        gitTask.remote = "origin"
        gitTask.pullRequest = pullRequest
        gitTask.zargs+='-zargs'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} fetch \"origin\" pull/${pullRequest}/head:pr-${pullRequest} -zargs".toString(), cmd)
    }
}
