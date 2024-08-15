package org.prodacc.webapi

import com.amazonaws.serverless.proxy.model.AwsProxyRequest
import com.amazonaws.serverless.proxy.model.AwsProxyResponse
import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import javax.ws.rs.core.Application

class LambdaHandler : RequestHandler<AwsProxyRequest, AwsProxyResponse> {
    private val handler: SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>

    /*init {
        try {
            handler = SpringLambdaContainerHandler.getAwsProxyHandler(Application::class.java)
        } catch (e: ContainerInitializationException) {
            throw RuntimeException("Could not initialize Spring Boot application", e)
        }
    }*/

    init {
        val classLoader = Thread.currentThread().contextClassLoader
        val clazz = classLoader.loadClass("com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler")
        val method = clazz.getMethod("getAwsProxyHandler", Class::class.java)
        handler = method.invoke(null, Application::class.java) as SpringLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse>
    }


    override fun handleRequest(input: AwsProxyRequest, context: Context): AwsProxyResponse {
        return handler.proxy(input, context)
    }
}