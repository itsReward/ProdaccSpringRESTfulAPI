package org.prodacc.webapi

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import java.io.InputStream
import java.io.OutputStream

class LambdaHandler : RequestStreamHandler {
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {}
}