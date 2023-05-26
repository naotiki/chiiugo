package me.naotiki.sucideaplugin

import SocketService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.refactoring.rename.RenameUsagesCollector.Companion.started
import kotlinx.coroutines.runBlocking

class PopupDialogAction : AnAction() {

    val server=ApplicationManager.getApplication().getService(SocketService::class.java)
    override fun update(e: AnActionEvent) {
        //e.presentation.isEnabled= !server.connecting
        //イベントを使用してコンテキストを評価し、アクションを有効または無効にします。

    }
    override fun actionPerformed(e: AnActionEvent) {
        println("Try ReConnect Server")
        runBlocking {
            server.startServer()
        }
        val project=e.project
        if (project!=null){
            server.sendData(
                ServerProtocol.SendEvent(
                    Event.OpenProject(project.name)
                ),project
            )
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
// 2022.3 以降をターゲットにする場合は、getActionUpdateThread() をオーバーライドしてください。
}