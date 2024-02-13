package org.dockit.dockitagent.config;


/**
 * Singleton configuration enum for config information of the agent.
 */
public enum Config {
    INSTANCE();

    private int INTERVAL;
    private boolean DOCKER;
    private boolean VM_DATA;
    private String KEY;
    private String ID;

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
    String getKEY() {
        return KEY;
    }


    /**
     * @param KEY secret key string
     */
    void setKEY(String KEY) {
        this.KEY = KEY;
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
}
