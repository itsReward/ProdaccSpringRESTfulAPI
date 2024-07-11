package org.prodacc.webapi.services

import io.jsonwebtoken.security.Keys
import org.prodacc.webapi.config.JwtProperties
import org.springframework.stereotype.Service


@Service
class TokenService (
    jwtProperties: JwtProperties
){

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
}