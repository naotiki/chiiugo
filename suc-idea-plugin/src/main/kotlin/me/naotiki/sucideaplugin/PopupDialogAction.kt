package me.naotiki.sucideaplugin

import SocketService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import kotlinx.coroutines.*

class PopupDialogAction : AnAction() {

    val server=ApplicationManager.getApplication().getService(SocketService::class.java)
    override fun update(e: AnActionEvent) {
        //e.presentation.isEnabled= !server.connecting
        //イベントを使用してコンテキストを評価し、アクションを有効または無効にします。

    }
    private val scope=CoroutineScope(Dispatchers.Default)
    override fun actionPerformed(e: AnActionEvent) {
        println("Try ReConnect Server")
        scope.launch {
            server.startServer()
            val project=e.project
            if (project!=null){
                server.sendData(
                    SocketProtocol.SendEvent(
                        Event.OpenProject(project.name)
                    )
                )
            }
        }

    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}