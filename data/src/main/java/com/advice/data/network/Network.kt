package com.advice.data.network

import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import timber.log.Timber
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object Network {
    val client: OkHttpClient by lazy {
        if (System.getProperty("com.android.tools.idea.preview") == "true") {
            return@lazy OkHttpClient()
        }

        try {
            val trustManagerFactory =
                TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm(),
                )
            trustManagerFactory.init(null as KeyStore?)
            val trustManagers = trustManagerFactory.trustManagers
            check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
                ("Unexpected default trust managers:" + trustManagers.contentToString())
            }
            val trustManager = trustManagers[0] as X509TrustManager

            val sslContext = SSLContext.getInstance(TlsVersion.TLS_1_3.javaName)
            sslContext.init(null, arrayOf<TrustManager>(trustManager), null)

            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            val connectionSpec =
                ConnectionSpec
                    .Builder(ConnectionSpec.RESTRICTED_TLS)
                    .tlsVersions(TlsVersion.TLS_1_3)
                    .cipherSuites(
                        CipherSuite.TLS_AES_256_GCM_SHA384,
                        CipherSuite.TLS_CHACHA20_POLY1305_SHA256,
                        CipherSuite.TLS_AES_128_GCM_SHA256,
                    ).build()

            OkHttpClient
                .Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .connectionSpecs(listOf(connectionSpec))
                .build()
        } catch (ex: Throwable) {
            // Fallback to a basic client if custom TLS configuration fails (e.g. in a JVM preview environment)
            Timber.e(ex, "Could not configure TLS; falling back to default OkHttpClient")
            OkHttpClient()
        }
    }
}
