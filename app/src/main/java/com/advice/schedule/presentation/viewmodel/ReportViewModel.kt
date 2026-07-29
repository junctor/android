package com.advice.schedule.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.network.NetworkResponse
import com.advice.core.utils.ToastData
import com.advice.core.utils.ToastManager
import com.advice.data.session.UserSession
import com.advice.feedback.network.ReportSubmissionRepository
import com.advice.feedback.network.models.ReportObjectType
import com.advice.ui.R
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ReportViewModel :
    ViewModel(),
    KoinComponent {
    private val context by inject<Context>()
    private val reportRepository by inject<ReportSubmissionRepository>()
    private val userSession by inject<UserSession>()
    private val toastManager by inject<ToastManager>()

    fun submit(
        message: String,
        objectType: ReportObjectType,
        objectId: Long,
    ) {
        viewModelScope.launch {
            val conference = userSession.currentConference
            if (conference == null) {
                Timber.e("Cannot submit report: no current conference")
                toastManager.push(
                    ToastData(context.getString(R.string.report_error)),
                )
                return@launch
            }

            when (
                reportRepository.submit(
                    message = message,
                    objectType = objectType,
                    objectId = objectId,
                    conference = conference,
                )
            ) {
                NetworkResponse.Success -> {
                    toastManager.push(
                        ToastData(context.getString(R.string.report_success)),
                    )
                }

                is NetworkResponse.Error -> {
                    toastManager.push(
                        ToastData(context.getString(R.string.report_error)),
                    )
                }
            }
        }
    }
}
