package org.prodacc.webapi

import com.amazonaws.serverless.exceptions.ContainerInitializationException
import com.amazonaws.serverless.proxy.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import javax.ws.rs.core.Application

class LambdaHandler : RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private val handler: SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>

    init {
        try {
            handler = SpringLambdaContainerHandler.getAwsProxyHandler(Application::class.java)
        } catch (e: ContainerInitializationException) {
            throw RuntimeException("Could not initialize Spring Boot application", e)
        }
    }

    override fun handleRequest(input: AwsProxyRequest, context: Context): AwsProxyResponse {
        return handler.proxy(input, context)
    }
}