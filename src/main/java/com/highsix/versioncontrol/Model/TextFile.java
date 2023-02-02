package com.highsix.versioncontrol.Model;

import java.util.List;

public class TextFile {

    private String name;
    private String createdAt;
    private List<FileVersion> versions;
    private int versionCount;
    private boolean locked;

    public TextFile() {
    }

    public TextFile(String name, String createdAt, List<FileVersion> versions, int versionCount, boolean locked) {
        this.name = name;
        this.createdAt = createdAt;
        this.versions = versions;
        this.versionCount = versionCount;
        this.locked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<FileVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<FileVersion> versions) {
        this.versions = versions;
    }

    public int getVersionCount() {
        return versionCount;
    }

    public void setVersionCount(int versionCount) {
        this.versionCount = versionCount;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean like(TextFile otherTextFile){
        if (!this.name.equals(otherTextFile.getName())) return false;
        if (!this.createdAt.equals(otherTextFile.getCreatedAt())) return false;
        if (this.versionCount != otherTextFile.getVersionCount()) return false;
        if (this.locked != otherTextFile.isLocked()) return false;
        for (FileVersion version : versions) {
            FileVersion otherVersion = otherTextFile.getVersions().get(version.getVersionId()-1);
            if (version.getVersionId() != otherVersion.getVersionId()) return false;
            if (!version.getFileContent().equals(otherVersion.getFileContent())) return false;
        }
        return true;
    }
}
