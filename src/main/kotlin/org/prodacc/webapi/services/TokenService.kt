package org.prodacc.webapi.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.prodacc.webapi.config.JwtProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

/**
 * This service provides methods for generating, validating, and extracting information from JWT (JSON Web Token) tokens.
 * It relies on a secret key configured in `JwtProperties` for signing and verifying tokens.
 *
 * The service offers the following functionalities:
 *
 * 1. `generate(userDetails, expirationDate, additionalClaims)`: Generates a new JWT token.
- Takes user details, an expiration date, and optional additional claims as arguments.
- Builds the token with claims like subject (username), issued at timestamp, expiration, and additional claims.
- Signs the token using the secret key.
- Returns the compact serialized JWT string.
 * 2. `extractUsername(token)`: Extracts the username claim from a JWT token.
- Parses the token and retrieves the "subject" claim, which is typically the username.
- Returns the username if found, or null if not present or the token is invalid.
 * 3. `isExpired(token)`: Checks if a JWT token has expired.
- Parses the token and retrieves the expiration claim.
- Compares the expiration with the current time.
- Returns true if the token is expired, false otherwise.
 * 4. `isValid(token, userDetails)`: Validates a JWT token against the provided user details.
- Extracts the username and authorities from the token.
- Compares the extracted username with the user details' username.
- Checks if the token is expired.
- Optionally compares the extracted authorities with the user details' authorities (if present in the token).
- Returns true if all conditions are met, false otherwise.
 * 5. `extractAuthorities(token)`: Attempts to extract a list of authorities from a JWT token.
- Parses the token and tries to retrieve the "authorities" claim as a list of strings.
- Returns the extracted list if successful, null if the claim is missing or not a list.
- Catches potential exceptions during parsing and returns null.
 */
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
        val tokenAuthorities = getAllClaimsFromToken(token).get("authorities", List::class.java)
        return username == userDetails.username &&
                !isExpired(token) && tokenAuthorities == userDetails.authorities.map { it.authority }
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        val parser = Jwts.parser().verifyWith(secretKey).build()

        return parser.parseSignedClaims(token).payload
    }


    fun extractAuthorities(token: String): List<String>? {
        return try {
            getAllClaimsFromToken(token).get("authorities", List::class.java) as? List<String>
        } catch (e: Exception) {
            null
        }
    }
}