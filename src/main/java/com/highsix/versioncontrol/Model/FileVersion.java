package com.highsix.versioncontrol.Model;

public class FileVersion {

    private int versionId;
    private String fileContent;
    private String lastUpdatedAt;

    public FileVersion() {
    }

    public FileVersion(int versionId, String fileContent, String lastUpdatedAt) {
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

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
