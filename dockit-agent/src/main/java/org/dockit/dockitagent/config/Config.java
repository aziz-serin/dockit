package org.dockit.dockitagent.config;


import java.time.ZoneId;

/**
 * Singleton configuration enum for config information of the agent.
 */
public enum Config {
    INSTANCE();

    private int INTERVAL;
    private boolean DOCKER;
    private boolean VM_DATA;
    private String SECRET_KEY;
    private String API_KEY;
    private String ID;
    private ZoneId ZONE_ID;
    private String DOCKER_URL;
    private String SERVER_URL;
    private String VM_ID;

    /**
     * @return an instance of {@link Config}
     */
    Config getInstance() {
        return INSTANCE;
    }

    /**
     * @return return audit request interval
     */
    public int getINTERVAL() {
        return INTERVAL;
    }

    /**
     * @param INTERVAL interval for sending audit requests in seconds
     */
    void setINTERVAL(int INTERVAL) {
        this.INTERVAL = INTERVAL;
    }

    /**
     * @return if Docker information should be audited or not
     */
    public boolean isDOCKER() {
        return DOCKER;
    }

    /**
     * @param DOCKER set true to audit docker information, false otherwise
     */
    void setDOCKER(boolean DOCKER) {
        this.DOCKER = DOCKER;
    }

    /**
     * @return if VM information should be audited or not
     */
    public boolean isVM_DATA() {
        return VM_DATA;
    }

    /**
     * @param VM_DATA set true to audit vm information, false otherwise
     */
    void setVM_DATA(boolean VM_DATA) {
        this.VM_DATA = VM_DATA;
    }

    /**
     * @return secret key string
     */
    String getSECRET_KEY() {
        return SECRET_KEY;
    }


    /**
     * @param SECRET_KEY secret key string
     */
    void setSECRET_KEY(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }

    /**
     * @return id for the agent
     */
    public String getID() {
        return ID;
    }

    /**
     * @param ID id for the agent
     */
    void setID(String ID) {
        this.ID = ID;
    }

    /**
     * @return specified zone, e.g. "Europe/London"
     */
    public ZoneId getZONE_ID() {
        return ZONE_ID;
    }

    /**
     * @param ZONE_ID specified zone, e.g. "Europe/London"
     */
    void setZONE_ID(ZoneId ZONE_ID) {
        this.ZONE_ID = ZONE_ID;
    }

    /**
     * @return url to send the docker api requests
     */
    public String getDOCKER_URL() {
        return DOCKER_URL;
    }

    /**
     * @param DOCKER_URL url to send the docker api requests
     */
    void setDOCKER_URL(String DOCKER_URL) {
        this.DOCKER_URL = DOCKER_URL;
    }

    /**
     * @return url to send the audit requests
     */
    public String getSERVER_URL() {
        return SERVER_URL;
    }

    /**
     * @param SERVER_URL url to send the audit requests
     */
    void setSERVER_URL(String SERVER_URL) {
        this.SERVER_URL = SERVER_URL;
    }


    /**
     * @return api key to be used for authentication
     */
    public String getAPI_KEY() {
        return API_KEY;
    }

    /**
     * @param API_KEY api key to be used for authentication
     */
    void setAPI_KEY(String API_KEY) {
        this.API_KEY = API_KEY;
    }

    /**
     * @return vm_id for the running vm
     */
    public String getVM_ID() {
        return VM_ID;
    }

    /**
     * @param VM_ID vm_id for the running vm
     */
    void setVM_ID(String VM_ID) {
        this.VM_ID = VM_ID;
    }
}
