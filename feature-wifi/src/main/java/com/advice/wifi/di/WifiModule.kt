package com.advice.wifi.di

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import com.advice.wifi.WirelessConnectionManager
import com.advice.wifi.data.repositories.WifiNetworkRepository
import com.advice.wifi.presentation.viewmodel.WifiViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wifiModule =
    module {
        single { WifiNetworkRepository(get()) }
        single<WirelessConnectionManager> {
            WirelessConnectionManager(
                androidContext().resources,
                androidContext().getSystemService(WIFI_SERVICE) as WifiManager,
            )
        }
        viewModel { WifiViewModel() }
    }
