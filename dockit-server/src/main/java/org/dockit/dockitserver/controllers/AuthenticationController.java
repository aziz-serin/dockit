package org.dockit.dockitserver.controllers;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import org.dockit.dockitserver.authentication.issuer.APIKeyIssuer;
import org.dockit.dockitserver.authentication.issuer.JwtIssuer;
import org.dockit.dockitserver.controllers.utils.ParameterValidator;
import org.dockit.dockitserver.security.jwt.JWTValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller containing the endpoints for authentication
 */
@RestController
@RequestMapping(path = "/api/authenticate", produces = {MediaType.APPLICATION_JSON_VALUE})
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    private final APIKeyIssuer apiKeyIssuer;
    private final JwtIssuer jwtIssuer;
    private final JWTValidator jwtValidator;

    /**
     * @param apiKeyIssuer {@link APIKeyIssuer} object to be injected
     * @param jwtIssuer {@link JwtIssuer} object to be injected
     * @param jwtValidator {@link JWTValidator} object to be injected
     */
    public AuthenticationController(APIKeyIssuer apiKeyIssuer, JwtIssuer jwtIssuer, JWTValidator jwtValidator) {
        this.apiKeyIssuer = apiKeyIssuer;
        this.jwtIssuer = jwtIssuer;
        this.jwtValidator = jwtValidator;
    }

    /**
     * Issue a new api key for an agent
     *
     * @param body should contain the parameters: <br>
     *             "username" -> {@link org.dockit.dockitserver.entities.Admin} userName to be validated for
     *             authentication <br>
     *             "password" -> {@link org.dockit.dockitserver.entities.Admin} password to be validated for
     *             authentication <br>
     *             "agentId" -> id of an {@link org.dockit.dockitserver.entities.Agent} to issue the api key to <br>
     * @return Response entity containing the response
     */
    @PostMapping("/apiKey")
    public ResponseEntity<?> issueApiKey(@RequestBody @NonNull Map<String, ?> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        UUID agentId = UUID.fromString((String) body.get("agentId"));
        if (ParameterValidator.invalid(username, password, agentId)) {
            logger.debug("Invalid request parameters in body: {}", body);
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<String> apiKey = apiKeyIssuer.issueKey(username, password, agentId);
        if (apiKey.isEmpty()) {
            logger.debug("Could not generate api key.");
            return ResponseEntity.badRequest().body("Invalid request!");
        } else {
            return ResponseEntity.ok().body(
                    Map.of(
                            "key", apiKey.get()
                    )
            );
        }
    }

    /**
     * Issue a new jwt for an admin
     *
     * @param body should contain the parameters: <br>
     *             "username" -> {@link org.dockit.dockitserver.entities.Admin} userName to be validated for
     *             authentication <br>
     *             "password" -> {@link org.dockit.dockitserver.entities.Admin} password to be
     *             validated for authentication <br>
     * @return Response entity containing the response
     */
    @PostMapping("/jwt")
    public ResponseEntity<?> issueJwt(@RequestBody @NonNull Map<String, ?> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        if (ParameterValidator.invalid(username, password)) {
            logger.debug("Invalid request parameters in body: {}", body);
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        Optional<String> jwt = jwtIssuer.issueJwt(username, password);
        if (jwt.isEmpty()) {
            logger.debug("Could not generate jwt");
            return ResponseEntity.badRequest().body("Invalid request!");
        } else {
            return ResponseEntity.ok().body(
                    Map.of(
                            "token", jwt.get()
                    )
            );
        }
    }


    /**
     * Send claims of the given jwt if it is valid
     *
     * @param body should contain the parameters: <br>
     *             "jwt" -> jwt to be introspected <br>
     * @return Response entity containing the response
     */
    @PostMapping("/jwt/introspect")
    public ResponseEntity<?> introspectJwt(@RequestBody @NonNull Map<String, ?> body) {
        String jwt = (String) body.get("jwt");
        if (ParameterValidator.invalid(jwt)) {
            logger.debug("Invalid request parameters in body: {}", body);
            return ResponseEntity.badRequest().body("Invalid request!");
        }
        try {
            if (!jwtValidator.validateJwtToken(jwt)) {
                return ResponseEntity.badRequest().body("Invalid request!");
            }
            JWT jwtObject = JWTParser.parse(jwt);
            return ResponseEntity.ok().body(jwtObject.getJWTClaimsSet().toJSONObject());
        } catch (ParseException e) {
            logger.debug("Could not parse jwt");
            return ResponseEntity.badRequest().body("Invalid request!");
        }
    }
}
