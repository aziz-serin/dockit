package org.dockit.dockitserver.events.listener;

import org.dockit.dockitserver.config.ConfigContainer;
import org.dockit.dockitserver.entities.Agent;
import org.dockit.dockitserver.entities.Alert;
import org.dockit.dockitserver.events.event.UserIntrusionEvent;
import org.dockit.dockitserver.mail.GmailEmailService;
import org.dockit.dockitserver.sender.AgentRequestSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserIntrusionEventListener {
    private static final String KICKED_OUT_MAIL = """
            Agent with id %s detected an intrusion by the user %s and successfully killed all the processes
            belonging to that user.\s

             ALERT LEVEL: %s""";

    private static final String NOT_KICKED_OUT_MAIL = """
            Agent with id %s detected an intrusion by the user %s but could not kill all the processes
            belonging to that user.\s

             ALERT LEVEL: %s""";
    private static final String AGENT_NOT_ALIVE_MAIl = """
            Agent with id %s detected an intrusion by the user %s but the agent is not responding to requests
            from the address: %s.\s

             ALERT LEVEL: %s""";

    private final AgentRequestSender agentRequestSender;
    private final GmailEmailService emailService;
    private final ConfigContainer configContainer;

    @Autowired
    public UserIntrusionEventListener(AgentRequestSender agentRequestSender, GmailEmailService emailService,
                                      ConfigContainer configContainer) {
        this.agentRequestSender = agentRequestSender;
        this.emailService = emailService;
        this.configContainer = configContainer;
    }

    @EventListener
    public void userIntrusionAlertListener(UserIntrusionEvent userIntrusionEvent) {
        Agent agent = userIntrusionEvent.agent();
        String userName = userIntrusionEvent.userName();

        if (agentRequestSender.isAgentServerAlive(agent)) {
            if (agentRequestSender.sendIntrusionRequest(agent, userName)) {
                sendUserKickedOutMail(agent, userName);
            } else {
                sendUserCouldNotBeKickedOutMail(agent, userName);
            }
        } else {
            sendAgentServerNotAliveMail(agent, userName);
        }
    }

    private void sendUserKickedOutMail(Agent agent, String userName) {
        emailService.sendEmail(agent, configContainer.getConfig().getSendingEmailAddress(),
                KICKED_OUT_MAIL.formatted(agent.getId(), userName, Alert.Importance.CRITICAL.toString()));
    }

    private void sendUserCouldNotBeKickedOutMail(Agent agent, String userName) {
        emailService.sendEmail(agent, configContainer.getConfig().getSendingEmailAddress(),
                NOT_KICKED_OUT_MAIL.formatted(agent.getId(), userName, Alert.Importance.CRITICAL.toString()));
    }

    private void sendAgentServerNotAliveMail(Agent agent, String userName) {
        emailService.sendEmail(agent, configContainer.getConfig().getSendingEmailAddress(),
                AGENT_NOT_ALIVE_MAIl.formatted(agent.getId(), userName, agent.getAgentUrl().toString(),
                        Alert.Importance.CRITICAL.toString()));
    }
}
