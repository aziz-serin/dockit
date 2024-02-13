package org.dockit.dockitagent.entity;

import org.dockit.dockitagent.exceptions.entity.AuditBuildingException;

import java.time.ZonedDateTime;

/**
 * Step-up builder for generating {@link Audit}
 */
public class AuditBuilder {
    private AuditBuilder() {}

    public static VmId newBuilder() {return new Builder();}

    public interface VmId {
        Category vmId(String vmId);
    }

    public interface Category {
        TimeStamp category(String category);
    }

    public interface TimeStamp {
        Data timeStamp(ZonedDateTime timeStamp);
    }

    public interface Data {
        Build data(String data);
    }

    public interface Build {
        Audit build();
    }

    private static class Builder implements VmId, Category, TimeStamp, Data, Build {

        private String vmId;
        private String category;
        private ZonedDateTime timeStamp;
        private String data;

        @Override
        public Category vmId(String vmId) {
            if (vmId == null) {
                throw new AuditBuildingException("vmId cannot be null!");
            }
            this.vmId = vmId;
            return this;
        }

        @Override
        public TimeStamp category(String category) {
            if (category == null) {
                throw new AuditBuildingException("category cannot be null!");
            }
            this.category = category;
            return this;
        }

        @Override
        public Data timeStamp(ZonedDateTime timeStamp) {
            if (timeStamp == null) {
                throw new AuditBuildingException("timeStamp cannot be null!");
            }
            this.timeStamp = timeStamp;
            return this;
        }

        @Override
        public Build data(String data) {
            if (data == null) {
                throw new AuditBuildingException("data cannot be null!");
            }
            this.data = data;
            return this;
        }

        @Override
        public Audit build() {
            return new Audit(
                    this.vmId,
                    this.category,
                    this.timeStamp,
                    this.data
            );
        }
    }
}
