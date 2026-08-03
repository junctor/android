package com.advice.feedback.presentation.viewmodel

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
import timber.log.Timber

class ReportViewModel(
    private val context: Context,
    private val reportRepository: ReportSubmissionRepository,
    private val userSession: UserSession,
    private val toastManager: ToastManager,
) : ViewModel() {
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
