package me.naotiki.sucideaplugin.listeners

import Event
import ServerProtocol
import SocketService
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.feedback.common.track.EditorTypingEventTracker
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.event.EditorEventListener
import com.intellij.openapi.editor.ex.DocumentBulkUpdateListener
import com.intellij.openapi.editor.impl.DocumentImpl
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.impl.EditorCompositeListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.*
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.pointers.VirtualFilePointer
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener
import com.intellij.psi.PsiFile
import com.intellij.refactoring.suggested.newRange
import com.intellij.ui.ColoredText.Fragment
import com.intellij.util.DocumentEventUtil
import com.intellij.util.DocumentUtil
import com.jetbrains.rd.util.string.println
import com.sun.istack.FragmentContentHandler
import org.jetbrains.plugins.notebooks.visualization.getText


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
    //IJの補完入力は愛がこもっていないので反応しない。
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        project.getService(SocketService::class.java).sendData(
            ServerProtocol.SendEvent(
                Event.Typed(c)
            ),project
        )
        return super.charTyped(c, project, editor, file)
    }
}
/*class DocLis :DocumentListener{
    override fun documentChanged(event: DocumentEvent) {
       // println(event.newFragment)
        val count=event.newLength-event.oldLength
        if (count>0){

            println(event.document.getText(TextRange(event.offset,event.offset+count)))
        }
    }
}*/
class ProjectStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        val started = project
            .getService(SocketService::class.java)
        started.startServer()
        started.sendData(ServerProtocol.Hello)
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
    }

}

data class FileType(val name:String,val extension:String)
