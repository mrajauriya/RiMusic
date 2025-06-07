package it.fast4x.environment.utils

import io.ktor.http.URLBuilder
import io.ktor.http.parseQueryString
import io.ktor.util.toMap
import it.fast4x.environment.Environment
import it.fast4x.environment.models.Context
import it.fast4x.environment.models.PlayerResponse
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.exceptions.ParsingException
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException
import org.schabi.newpipe.extractor.services.youtube.YoutubeJavaScriptPlayerManager
import java.io.IOException
import java.net.Proxy

private class NewPipeDownloaderImpl(proxy: Proxy?) : Downloader() {

    private val client = OkHttpClient.Builder()
        .proxy(proxy)
        .build()

    @Throws(IOException::class, ReCaptchaException::class)
    override fun execute(request: Request): Response {
        val httpMethod = request.httpMethod()
        val url = request.url()
        val headers = request.headers()
        val dataToSend = request.dataToSend()

        val requestBuilder = okhttp3.Request.Builder()
            .method(httpMethod, dataToSend?.toRequestBody())
            .url(url)
            .addHeader("User-Agent", Context.USER_AGENT)

        headers.forEach { (headerName, headerValueList) ->
            if (headerValueList.size > 1) {
                requestBuilder.removeHeader(headerName)
                headerValueList.forEach { headerValue ->
                    requestBuilder.addHeader(headerName, headerValue)
                }
            } else if (headerValueList.size == 1) {
                requestBuilder.header(headerName, headerValueList[0])
            }
        }

        val response = client.newCall(requestBuilder.build()).execute()

        if (response.code == 429) {
            response.close()

            throw ReCaptchaException("NewPipe in Environment reCaptcha Challenge requested", url)
        }

        val responseBodyToReturn = response.body?.string()

        val latestUrl = response.request.url.toString()
        return Response(response.code, response.message, response.headers.toMultimap(), responseBodyToReturn, latestUrl)
    }

}

object NewPipeUtils {

    init {
        NewPipe.init(NewPipeDownloaderImpl(Environment.proxy))
    }

    fun getSignatureTimestamp(videoId: String): Result<Int> = runCatching {
        YoutubeJavaScriptPlayerManager.getSignatureTimestamp(videoId)
    }

    fun getStreamUrl(format: PlayerResponse.StreamingData.Format, videoId: String): Result<String> =
        runCatching {
            format.url?.let {
                return@runCatching it
            }
            format.signatureCipher.let {
                if (it == null) throw ParsingException("NewPipe in Environment Could not find format signatureCipher")
                return@runCatching decodeSignatureCipher(videoId, it)
            }
//            format.signatureCipher?.let { signatureCipher ->
//                val params = parseQueryString(signatureCipher)
//                println("NewPipe in Environment getStreamUrl params ${params.toMap().map { it.key }}")
//                val obfuscatedSignature = params["s"]
//                    ?: throw ParsingException("NewPipe in Environment Could not parse cipher signature")
//                val signatureParam = params["sp"]
//                    ?: throw ParsingException("NewPipe in Environment Could not parse cipher signature parameter")
//                val url = params["url"]?.let { URLBuilder(it) }
//                    ?: throw ParsingException("NewPipe in Environment Could not parse cipher url")
//                url.parameters[signatureParam] =
//                    YoutubeJavaScriptPlayerManager.deobfuscateSignature(
//                        videoId,
//                        obfuscatedSignature
//                    )
//                println("NewPipe in Environment getStreamUrl url.parameters ${url.parameters.entries().map { it.key }}")
//
//                return@runCatching YoutubeJavaScriptPlayerManager.getUrlWithThrottlingParameterDeobfuscated(
//                    videoId,
//                    url.toString()
//                )
//            }
            //throw ParsingException("NewPipe in Environment Could not find format url")
        }

    fun decodeSignatureCipher(
        videoId: String,
        signatureCipher: String,
    ): String =
        signatureCipher.let { signature ->
            val params = parseQueryString(signature)
            println("NewPipe in Environment decodeSignatureCipher params ${params.toMap().map { it.key }}")
            val obfuscatedSignature = params["s"]
                ?: throw ParsingException("NewPipe in Environment decodeSignatureCipher Could not parse cipher signature")
            val signatureParam = params["sp"]
                ?: throw ParsingException("NewPipe in Environment decodeSignatureCipher Could not parse cipher signature parameter")
            val url = params["url"]?.let { URLBuilder(it) }
                ?: throw ParsingException("NewPipe in Environment decodeSignatureCipher Could not parse cipher url")
            url.parameters[signatureParam] =
                YoutubeJavaScriptPlayerManager.deobfuscateSignature(
                    videoId,
                    obfuscatedSignature
                )
            println("NewPipe in Environment decodeSignatureCipher url.parameters ${url.parameters.entries().map { it.key }}")

            YoutubeJavaScriptPlayerManager.getUrlWithThrottlingParameterDeobfuscated(
                videoId,
                url.toString()
            )
        }
//        try {
//            val params = parseQueryString(signatureCipher)
//            println("NewPipe decodeSignatureCipher params $params")
//            val obfuscatedSignature = params["s"] ?: throw ParsingException("NewPipe in Environment Could not parse cipher signature")
//            val signatureParam = params["sp"] ?: throw ParsingException("NewPipe in Environment Could not parse cipher signature parameter")
//            val url = params["url"]?.let { URLBuilder(it) } ?: throw ParsingException("NewPipe in Environment Could not parse cipher url")
//            url.parameters[signatureParam] = YoutubeJavaScriptPlayerManager.deobfuscateSignature(videoId, obfuscatedSignature)
//            print("NewPipe in Environment decodeSignatureCipher URL $url")
//            YoutubeJavaScriptPlayerManager.getUrlWithThrottlingParameterDeobfuscated(videoId, url.toString())
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }

}