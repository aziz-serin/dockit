package org.dockit.dockitagent.command;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.dockit.dockitagent.encryption.AESGCMEncrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

public class CommandTranslator {

    private static final Logger logger = LoggerFactory.getLogger(CommandTranslator.class);

    private final AESGCMEncrypt encrypt;

    @Inject
    public CommandTranslator(AESGCMEncrypt encrypt) {
        this.encrypt = encrypt;
    }

    public Optional<Command> getCommand(String data) {
        try {
            Gson gson = new Gson();
            Map<String, String> commandData = gson.fromJson(data, Map.class);
            String encryptedData = commandData.get(CommandConstants.DATA);
            if (encryptedData == null) {
                return Optional.empty();
            }
            String decryptedData = encrypt.decrypt(encryptedData);

            Map<String, String> jsonData = gson.fromJson(decryptedData, Map.class);
            String command = jsonData.get(CommandConstants.COMMAND);
            String arguments = jsonData.get(CommandConstants.ARGUMENTS);
            if (command == null || arguments == null) {
                logger.error("Data does not contain required parameters command and argument!");
                return Optional.empty();
            }
            return Optional.of(new Command(command, arguments));
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }
}
