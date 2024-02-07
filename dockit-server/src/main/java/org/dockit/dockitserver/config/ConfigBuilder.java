package org.dockit.dockitserver.config;

/**
 * Step-up builder object to be used when generating configuration
 */
public class ConfigBuilder {

    private ConfigBuilder() {}

    public static MaxAgentCacheSize newBuilder() {
        return new Builder();
    }

    public interface MaxAgentCacheSize {
        MaxAuditCacheSize maxAgentCacheSize(Long maxAgentCacheSize);
    }

    public interface MaxAuditCacheSize {
        MaxAdminCacheSize maxAuditCacheSize(Long maxAuditCacheSize);
    }

    public interface MaxAdminCacheSize {
        MaxAccessTokenCacheSize maxAdminCacheSize(Long maxAdminCacheSize);
    }

    public interface MaxAccessTokenCacheSize {
        MaxAgentSize maxAccessTokenCacheSize(Long maxAccessTokenCacheSize);
    }

    public interface MaxAgentSize {
        KeyStorePassword maxAgentSize(Integer maxAgentSize);
    }

    public interface KeyStorePassword {
        JwtIssuer keyStorePassword(String keyStorePassword);
    }

    public interface JwtIssuer {
        JwtSecretAlias jwtIssuer(String jwtIssuer);
    }

    public interface JwtSecretAlias {
        JwtExpirationTime jwtSecretAlias(String jwtSecretAlias);
    }

    public interface JwtExpirationTime {
        Build jwtExpirationTime(Integer jwtExpirationTime);
    }

    public interface Build {
        Config build();
    }

    private static class Builder implements MaxAgentCacheSize, MaxAuditCacheSize, MaxAdminCacheSize,
            MaxAccessTokenCacheSize, MaxAgentSize, KeyStorePassword, JwtIssuer, JwtSecretAlias, JwtExpirationTime,
            Build {

        private Long maxAgentCacheSize;
        private Long maxAuditCacheSize;
        private Long maxAdminCacheSize;
        private Long maxAccessTokenCacheSize;
        private Integer maxAgentSize;
        private String keyStorePassword;
        private String jwtIssuer;
        private String jwtSecretAlias;
        private Integer jwtExpirationTime;

        @Override
        public MaxAuditCacheSize maxAgentCacheSize(Long maxAgentCacheSize) {
            this.maxAgentCacheSize = maxAgentCacheSize;
            return this;
        }

        @Override
        public MaxAdminCacheSize maxAuditCacheSize(Long maxAuditCacheSize) {
            this.maxAuditCacheSize = maxAuditCacheSize;
            return this;
        }

        @Override
        public MaxAccessTokenCacheSize maxAdminCacheSize(Long maxAdminCacheSize) {
            this.maxAdminCacheSize = maxAdminCacheSize;
            return this;
        }

        @Override
        public MaxAgentSize maxAccessTokenCacheSize(Long maxAccessTokenCacheSize) {
            this.maxAccessTokenCacheSize = maxAccessTokenCacheSize;
            return this;
        }

        @Override
        public KeyStorePassword maxAgentSize(Integer maxAgentSize) {
            this.maxAgentSize = maxAgentSize;
            return this;
        }

        @Override
        public JwtIssuer keyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        @Override
        public JwtSecretAlias jwtIssuer(String jwtIssuer) {
            this.jwtIssuer = jwtIssuer;
            return this;
        }

        @Override
        public JwtExpirationTime jwtSecretAlias(String jwtSecretAlias) {
            this.jwtSecretAlias = jwtSecretAlias;
            return this;
        }

        @Override
        public Build jwtExpirationTime(Integer jwtExpirationTime) {
            this.jwtExpirationTime = jwtExpirationTime;
            return this;
        }

        @Override
        public Config build() {
            return new Config(
                    this.maxAgentCacheSize,
                    this.maxAuditCacheSize,
                    this.maxAdminCacheSize,
                    this.maxAccessTokenCacheSize,
                    this.maxAgentSize,
                    this.keyStorePassword,
                    this.jwtIssuer,
                    this.jwtSecretAlias,
                    this.jwtExpirationTime
            );
        }
    }
}
