package org.dockit.dockitagent.sender;

import com.google.inject.Inject;
import org.dockit.dockitagent.connection.ServerConnectionManager;
import org.dockit.dockitagent.entity.Audit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Component responsible to send {@link Audit}s to server
 */
public class AuditSender implements Sender {
    private static final Logger logger = LoggerFactory.getLogger(AuditSender.class);
    private static final String ENDPOINT = "/api/write";

    private final ServerConnectionManager serverConnectionManager;

    /**
     * @param serverConnectionManager {@link ServerConnectionManager} instance to be injected
     */
    @Inject
    public AuditSender(ServerConnectionManager serverConnectionManager) {
        this.serverConnectionManager = serverConnectionManager;
    }

    /**
     * Take {@link Audit} as an input, convert it to json string and use that as a body to send a POST request to
     * write endpoint of the server. Indicate success or failure with the return value
     *
     * @param audit {@link Audit} data
     * @return true if successfully sent, false otherwise
     */
    public boolean send(Audit audit) {
        if (serverConnectionManager.isAlive()) {
            Optional<String> response = serverConnectionManager.sendRequest(ENDPOINT, audit.toMap());
            if (response.isPresent()) {
                logger.info("Sent request for audit to server successfully.");
                return true;
            } else {
                logger.error("Could not send the request for audit, an error occurred!");
                return false;
            }
        }
        logger.error("Could not send the request for audit, server is not alive/ready!");
        return false;
    }
}
