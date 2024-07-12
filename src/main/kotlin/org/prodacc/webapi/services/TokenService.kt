package org.prodacc.webapi.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.prodacc.webapi.config.JwtProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*


@Service
class TokenService(
    jwtProperties: JwtProperties
) {

    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generate(
        userDetails: UserDetails, expirationDate: Date, additionalClaims: Map<String, Any> = emptyMap()
    ): String = Jwts.builder().claims().subject(userDetails.username).issuedAt(Date(System.currentTimeMillis()))
        .expiration(expirationDate).add(additionalClaims).and().signWith(secretKey).compact()

    fun extractUsername(token: String): String? = getAllClaimsFromToken(token).subject

    fun isExpired(token: String): Boolean =
        getAllClaimsFromToken(token).expiration.before(Date(System.currentTimeMillis()))

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return userDetails.username == username && isExpired(token)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        val parser = Jwts.parser().verifyWith(secretKey).build()

        return parser.parseSignedClaims(token).payload
    }
}