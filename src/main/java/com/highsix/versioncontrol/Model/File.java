package com.highsix.versioncontrol.Model;

import java.time.LocalDateTime;
import java.util.List;

public class File {

    private String name;
    private String author;
    private LocalDateTime createdAt;
    private List<FileVersion> versions;
    private int versionCount;
    private boolean locked;
    private String logPassword;

    public File() {
    }

    public File(String name, String author, LocalDateTime createdAt, List<FileVersion> versions, int versionCount, boolean locked, String logPassword) {
        this.name = name;
        this.author = author;
        this.createdAt = createdAt;
        this.versions = versions;
        this.versionCount = versionCount;
        this.locked = locked;
        this.logPassword = logPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
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

    public String getLogPassword() {
        return logPassword;
    }

    public void setLogPassword(String logPassword) {
        this.logPassword = logPassword;
    }
}
