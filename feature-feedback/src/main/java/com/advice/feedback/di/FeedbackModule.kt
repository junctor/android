package com.advice.feedback.di

import com.advice.feedback.data.repositories.FeedbackFormRepository
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.network.ReportSubmissionRepository
import com.advice.feedback.presentation.viewmodel.FeedbackViewModel
import com.advice.feedback.presentation.viewmodel.ReportViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun feedbackModule(versionName: String) =
    module {
        single { FeedbackFormRepository(get()) }
        single { FeedbackSubmissionRepository(versionName, get()) }
        single { ReportSubmissionRepository(versionName, get()) }
        viewModel { FeedbackViewModel() }
        viewModel { ReportViewModel() }
    }
