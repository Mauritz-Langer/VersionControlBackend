package com.highsix.versioncontrol.Model;

import java.time.LocalDateTime;

public class FileVersion {

    private int versionId;
    private String fileContent;
    private LocalDateTime lastUpdatedAt;

    public FileVersion() {
    }

    public FileVersion(int versionId, String fileContent, LocalDateTime lastUpdatedAt) {
        this.versionId = versionId;
        this.fileContent = fileContent;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
