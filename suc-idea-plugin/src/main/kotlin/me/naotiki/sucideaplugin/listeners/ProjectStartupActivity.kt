package me.naotiki.sucideaplugin.listeners

import Event
import SocketService
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.startup.StartupActivity
import com.intellij.util.application
import kotlinx.coroutines.runBlocking

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
        socketService.sendData(SocketProtocol.SendEvent(Event.CloseProject))
    }

    override fun runActivity(project: Project) {

        runBlocking {
            socketService.startServer()
        }
        socketService.sendData(
            SocketProtocol.SendEvent(
                Event.OpenProject(project.name)
            )
        )
        println("Server Started : $socketService")
    }

}