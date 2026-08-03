package com.advice.feedback.di

import com.advice.feedback.data.repositories.FeedbackFormRepository
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.network.ReportSubmissionRepository
import com.advice.feedback.presentation.viewmodel.FeedbackViewModel
import com.advice.feedback.presentation.viewmodel.ReportViewModel
import com.advice.feedback.storage.OfflineQueueStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun feedbackModule(versionName: String) =
    module {
        single { OfflineQueueStore(androidContext(), get()) }
        single { FeedbackFormRepository(get()) }
        single { FeedbackSubmissionRepository(versionName, get(), get()) }
        single { ReportSubmissionRepository(versionName, get(), get()) }
        viewModel { FeedbackViewModel(get(), get(), get()) }
        viewModel { ReportViewModel(androidContext(), get(), get(), get()) }
    }
