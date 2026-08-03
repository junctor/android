package com.advice.data.di

/**
 * Koin qualifier for the process-lifetime [kotlinx.coroutines.CoroutineScope].
 * The scope itself is created by the app's shell DI module; infra/feature modules
 * resolve it via `get(named(APPLICATION_SCOPE))`.
 */
const val APPLICATION_SCOPE = "applicationScope"
