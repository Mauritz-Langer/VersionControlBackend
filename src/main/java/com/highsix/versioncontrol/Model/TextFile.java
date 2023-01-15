package com.highsix.versioncontrol.Model;

import java.time.LocalDateTime;
import java.util.List;

public class TextFile {

    private String name;
    private String author;
    private String createdAt;
    private List<FileVersion> versions;
    private int versionCount;
    private boolean locked;
    private String logPassword;
    private String content;

    public TextFile() {
    }

    public TextFile(String content, String name, String author, String createdAt) {
        this.content = content;
        this.name = name;
        this.author = author;
        this.createdAt = createdAt;

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

    public String getLogPassword() {
        return logPassword;
    }

    public void setLogPassword(String logPassword) {
        this.logPassword = logPassword;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
