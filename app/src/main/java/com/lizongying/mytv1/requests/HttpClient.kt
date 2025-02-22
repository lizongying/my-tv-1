package com.lizongying.mytv1.requests


import android.os.Build
import android.util.Log
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


object HttpClient {
    const val TAG = "HttpClient"
    const val HOST = "https://www.gitlink.org.cn/lizongying/my-tv-1/raw/"
    const val DOWNLOAD_HOST = "https://www.gitlink.org.cn/lizongying/my-tv-1/releases/download/"

    val okHttpClient: OkHttpClient by lazy {
        getUnsafeOkHttpClient()
    }

    private fun OkHttpClient.Builder.enableTls12OnPreLollipop() {
        if (Build.VERSION.SDK_INT < 22) {
            try {
                val sslContext = SSLContext.getInstance("TLSv1.2")
                sslContext.init(null, null, java.security.SecureRandom())

                val trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm()
                )
                trustManagerFactory.init(null as KeyStore?)
                val trustManagers = trustManagerFactory.trustManagers
                val trustManager = trustManagers[0] as X509TrustManager

                sslSocketFactory(Tls12SocketFactory(sslContext.socketFactory), trustManager)
                connectionSpecs(
                    listOf(
                        ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .build(),
                        ConnectionSpec.COMPATIBLE_TLS,
                        ConnectionSpec.CLEARTEXT
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "enableTls12OnPreLollipop", e)
            }
        }
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            val trustManager =
                object : X509TrustManager {
                    override fun checkClientTrusted(
                        chain: Array<out java.security.cert.X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<out java.security.cert.X509Certificate>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return emptyArray()
                    }
                }

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf(trustManager), java.security.SecureRandom())

            return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { _, _ -> true }
                .dns(DnsCache())
                .apply { enableTls12OnPreLollipop() }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}