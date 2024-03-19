package me.naotiki.sucideaplugin.listeners

import Event
import SocketService
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.util.application

class SUCTypedHandler : TypedHandlerDelegate() {
    private val socket: SocketService = application.getService(SocketService::class.java)

    //IJの補完入力は愛がこもっていないので反応しない。
    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        socket.sendData(
            SocketProtocol.SendEvent(
                Event.Typed(c)
            )
        )
        return super.charTyped(c, project, editor, file)
    }
}