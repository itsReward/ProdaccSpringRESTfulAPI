package org.prodacc.webapi

import com.amazonaws.serverless.exceptions.ContainerInitializationException
import com.amazonaws.serverless.proxy.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import java.io.InputStream
import java.io.OutputStream
import javax.ws.rs.core.Application

class LambdaHandler : RequestStreamHandler {
    private val handler: SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>

    init {
        try {
            handler = SpringLambdaContainerHandler.getAwsProxyHandler(Application::class.java)
        } catch (e: ContainerInitializationException) {
            throw RuntimeException("Could not initialize Spring Boot application", e)
        }
    }

    override fun handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context) {
        handler.proxyStream(inputStream, outputStream, context)
    }
}