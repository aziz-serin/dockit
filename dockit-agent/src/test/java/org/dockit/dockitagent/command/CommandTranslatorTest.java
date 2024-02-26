package org.dockit.dockitagent.command;

import com.google.gson.Gson;
import org.dockit.dockitagent.encryption.AESGCMEncrypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandTranslatorTest {
    private static final String ARGUMENTS = "some_arguments";

    @Mock
    private AESGCMEncrypt encrypt;

    @Test
    public void getCommandReturnsEmptyGivenErrorWithDecryption() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        // Throw any exception
        when(encrypt.decrypt(any())).thenThrow(NoSuchAlgorithmException.class);
        Gson gson = new Gson();

        Map<String, String> encryptedData = Map.of(
                CommandConstants.COMMAND, CommandConstants.INTRUSION,
                CommandConstants.ARGUMENTS, ARGUMENTS
        );
        String encryptedDataJson = gson.toJson(encryptedData);

        Map<String, ?> requestJson = Map.of(
                CommandConstants.DATA, encryptedDataJson
                );
        String json = gson.toJson(requestJson);

        CommandTranslator commandTranslator = new CommandTranslator(encrypt);
        assertThat(commandTranslator.getCommand(json)).isEmpty();
    }

    @Test
    public void getCommandReturnsEmptyGivenErrorWithParsingEncryptedData() {
        // Throw any exception

        Gson gson = new Gson();

        Map<String, String> encryptedData = Map.of(
                CommandConstants.COMMAND, CommandConstants.INTRUSION,
                CommandConstants.ARGUMENTS, ARGUMENTS
        );
        String encryptedDataJson = gson.toJson(encryptedData);
        Map<String, ?> requestJson = Map.of(
                "some_other_key", encryptedDataJson
        );
        String json = gson.toJson(requestJson);


        CommandTranslator commandTranslator = new CommandTranslator(encrypt);
        assertThat(commandTranslator.getCommand(json)).isEmpty();
    }

    @Test
    public void getCommandReturnsEmptyGivenErrorWithParsingCommand() throws InvalidAlgorithmParameterException,
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException,
            InvalidKeyException {
        // Throw any exception
        Gson gson = new Gson();

        Map<String, String> encryptedData = Map.of(
                "some_key", CommandConstants.INTRUSION,
                CommandConstants.ARGUMENTS, ARGUMENTS
        );
        String encryptedDataJson = gson.toJson(encryptedData);

        Map<String, ?> requestJson = Map.of(
                CommandConstants.DATA, encryptedDataJson
        );
        String json = gson.toJson(requestJson);

        when(encrypt.decrypt(encryptedDataJson)).thenReturn(encryptedDataJson);

        CommandTranslator commandTranslator = new CommandTranslator(encrypt);
        assertThat(commandTranslator.getCommand(json)).isEmpty();
    }

    @Test
    public void getCommandReturnsCommand() throws InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Throw any exception
        Gson gson = new Gson();

        Map<String, String> encryptedData = Map.of(
                CommandConstants.COMMAND, CommandConstants.INTRUSION,
                CommandConstants.ARGUMENTS, ARGUMENTS
        );
        String encryptedDataJson = gson.toJson(encryptedData);

        Map<String, ?> requestJson = Map.of(
                CommandConstants.DATA, encryptedDataJson
        );
        String json = gson.toJson(requestJson);

        when(encrypt.decrypt(encryptedDataJson)).thenReturn(encryptedDataJson);

        CommandTranslator commandTranslator = new CommandTranslator(encrypt);
        Optional<Command> command = commandTranslator.getCommand(json);

        assertThat(command).isPresent();
        assertThat(command.get().alias()).isEqualTo(CommandConstants.KILL_USER);
        assertThat(command.get().argument()).isEqualTo(ARGUMENTS);
    }

}
