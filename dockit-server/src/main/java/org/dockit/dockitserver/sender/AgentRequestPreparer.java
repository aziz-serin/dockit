package org.dockit.dockitserver.sender;

import com.google.gson.Gson;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.security.encryption.AESGCMEncryptor;
import org.dockit.dockitserver.security.keystore.KeyStoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.net.URI;
import java.net.http.HttpRequest;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

@Component
public class AgentRequestPreparer {
    private static final Logger logger = LoggerFactory.getLogger(AgentRequestPreparer.class);

    private final KeyStoreHandler keyStoreHandler;

    @Autowired
    public AgentRequestPreparer(KeyStoreHandler keyStoreHandler) {
        this.keyStoreHandler = keyStoreHandler;
    }

    public Optional<HttpRequest> prepareIntrusionRequest(Agent agent, String userName) {
        Gson gson = new Gson();

        String commandJson = gson.toJson(Map.of(
                AgentRequestConstants.COMMAND, AgentRequestConstants.INTRUSION,
                AgentRequestConstants.ARGUMENTS, userName
        ));

        Optional<Key> key = keyStoreHandler.getKey(agent.getId().toString(), agent.getPassword().toCharArray());
        if (key.isEmpty()) {
            return Optional.empty();
        }

        String encryptedText;

        try {
            encryptedText = AESGCMEncryptor.encrypt(commandJson, agent.getId().toString(), (SecretKey) key.get());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error("Could not encrypt request json!");
            return Optional.empty();
        }

        Map<String, String> body = Map.of(
                AgentRequestConstants.DATA, encryptedText
        );

        HttpRequest request = HttpRequest.newBuilder(URI.create(agent.getAgentUrl()
                        + AgentRequestConstants.COMMAND_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .headers("accept", "application/json",
                        "Content-Type", "application/json")
                .build();

        return (Optional.of(request));
    }
}
