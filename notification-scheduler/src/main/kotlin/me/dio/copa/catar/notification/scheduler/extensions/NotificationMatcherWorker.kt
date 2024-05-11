package me.dio.copa.catar.notification.scheduler.extensions

import android.content.Context
import androidx.work.*
import me.dio.copa.catar.domain.model.MatchDomain
import java.lang.IllegalArgumentException
import java.time.Duration
import java.time.LocalDateTime

private const val NOTIFICATION_TITLE_KEY = "NOTIFICATION_TITLE_KEY"
private const val NOTIFICATION_CONTENT_KEY = "NOTIFICATION_CONTENT_KEY"

class NotificationMatcherWorker(private val context: Context, workerParans: WorkerParameters) :
        Worker(context, workerParans){
    override fun doWork(): Result {
        val title =inputData.getString(NOTIFICATION_TITLE_KEY) ?: throw IllegalArgumentException("Title is requered!")
        val content =inputData.getString(NOTIFICATION_CONTENT_KEY) ?: throw IllegalArgumentException("Content is requered!")

        context.showNotification(title, content)

        return Result.success()
    }

    companion object {
        fun start(context: Context, match: MatchDomain){
            val (id, _, _, team1, team2, matchData) = match
            val initialDelay = Duration.between(LocalDateTime.now(), matchData).minusMinutes(5)
            val inputData = workDataOf(
                NOTIFICATION_TITLE_KEY to "o jogo já vai começar",
                NOTIFICATION_CONTENT_KEY to "Hoje tem ${team1.flag} x ${team2.flag}")

            WorkManager.getInstance(context).enqueueUniqueWork(
                id, ExistingWorkPolicy.KEEP, createRequest(initialDelay, inputData))
        }

        private fun createRequest(initialDelay: Duration, inputData: Data): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<NotificationMatcherWorker>().setInitialDelay(initialDelay)
                .setInputData(inputData).build()

        fun cancel(context: Context, match: MatchDomain){
            WorkManager.getInstance(context).cancelUniqueWork(match.id)
        }
    }

}