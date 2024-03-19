package me.naotiki.sucideaplugin.listeners

import Event
import SocketProtocol.SendEvent
import SocketService
import com.intellij.execution.ExecutionListener
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.application


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

class TestListener : FileEditorManagerListener {
    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        file.fileType.name
    }

}


data class FileType(val name: String, val extension: String)
