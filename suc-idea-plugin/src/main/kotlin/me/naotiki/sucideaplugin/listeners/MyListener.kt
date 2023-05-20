package me.naotiki.sucideaplugin.listeners

import Event
import ServerProtocol
import SocketService
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.util.application


class MyListener(val project: Project) : ExecutionListener {
    val socketService = project.getService(SocketService::class.java)

    //ビルド開始
    override fun processStartScheduled(executorId: String, env: ExecutionEnvironment) {
        socketService.sendData(
            ServerProtocol.SendEvent(
                Event.StartBuild(executorId)
            ),project
        )

    }

    override fun processNotStarted(executorId: String, env: ExecutionEnvironment, cause: Throwable?) {
        socketService.sendData(
            ServerProtocol.SendEvent(
                Event.FailedBuild(executorId)
            ),project
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
            ServerProtocol.SendEvent(
                if (exitCode == 0)
                    Event.SuccessBuild(executorId)
                else Event.FailedBuild(executorId)
            ),project
        )
    }

}

class SUCTypedHandler : TypedHandlerDelegate() {
    private val socket: SocketService = application.getService(SocketService::class.java)
    //IJの補完入力は愛がこもっていないので反応しない。
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        socket.sendData(
            ServerProtocol.SendEvent(
                Event.Typed(c)
            ),project
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
class ProjectStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        val started = application
            .getService(SocketService::class.java)
        started.startServer()
        started.sendData(
            ServerProtocol.SendEvent(
                Event.OpenProject(project.name)
            ),project
        )
        println("Server Started : $started")
    }

}
class TestListener: FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        file.fileType.name
    }

}

data class FileType(val name:String,val extension:String)
