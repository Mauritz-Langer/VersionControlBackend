package com.highsix.versioncontrol.Model;

import java.time.LocalDateTime;

public class FileVersion {

    private int version;
    private byte[] fileContent;

    private String lastEditedBy;
    private LocalDateTime lastUpdatedAt;

    public FileVersion(int version, byte[] fileContent, String lastEditedBy, LocalDateTime lastUpdatedAt) {
        this.version = version;
        this.fileContent = fileContent;
        this.lastEditedBy = lastEditedBy;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(String lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
