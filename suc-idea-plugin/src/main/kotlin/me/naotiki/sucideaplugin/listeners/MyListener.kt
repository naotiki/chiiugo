package me.naotiki.sucideaplugin.listeners

import Event
import SocketProtocol.SendEvent
import SocketService
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.util.application
import kotlinx.coroutines.runBlocking


class MyListener(val project: Project) : ExecutionListener {
    val socketService = application.getService(SocketService::class.java)

    //ビルド開始
    override fun processStartScheduled(executorId: String, env: ExecutionEnvironment) {
        socketService.sendData(
            SendEvent(
                Event.StartBuild(executorId)
            )
        )

    }

    override fun processNotStarted(executorId: String, env: ExecutionEnvironment, cause: Throwable?) {
        socketService.sendData(
            SendEvent(
                Event.FailedBuild(executorId)
            )
        )
    }

    //ビルド終了
    override fun processTerminated(
        executorId: String,
        env: ExecutionEnvironment,
        handler: ProcessHandler,
        exitCode: Int
    ) {
        socketService.sendData(
            SendEvent(
                if (exitCode == 0)
                    Event.SuccessBuild(executorId)
                else Event.FailedBuild(executorId)
            )
        )
    }

}

class SUCTypedHandler : TypedHandlerDelegate() {
    private val socket: SocketService = application.getService(SocketService::class.java)

    //IJの補完入力は愛がこもっていないので反応しない。
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        socket.sendData(
            SendEvent(
                Event.Typed(c)
            )
        )
        return super.charTyped(c, project, editor, file)
    }
}

/*class DocLis :DocumentListener{
    override fun documentChanged(event: DocumentEvent) {
        println(event.newFragment.lines()[0])
        val count=event.newLength-event.oldLength
        if (count>0){

            println(event.document.getText(TextRange(event.offset,event.offset+count)))
        }
    }
}*/
class ProjectStartupActivity : StartupActivity, ProjectManagerListener {
    val socketService = application
        .getService(SocketService::class.java)

    override fun projectClosed(project: Project) {
        socketService.sendData(SendEvent(Event.CloseProject))
    }

    override fun runActivity(project: Project) {

        runBlocking {
            socketService.startServer()
        }
        socketService.sendData(
            SendEvent(
                Event.OpenProject(project.name)
            )
        )
        println("Server Started : $socketService")
    }

}

class TestListener : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        file.fileType.name
    }

}


data class FileType(val name: String, val extension: String)
