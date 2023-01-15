package com.highsix.versioncontrol.Service;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.highsix.versioncontrol.Model.FileVersion;
import com.highsix.versioncontrol.Model.TextFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;


@Service
@Component("firebaseStorage")
public class FirebaseStorage {
    private final Logger log = LoggerFactory.getLogger(FirebaseStorage.class);

    private StorageOptions storageOptions;
    private String bucketName = "versionverwaltung.appspot.com";
    private String projectId = "versionverwaltung";

    @PostConstruct
    public void initialize() throws IOException {

        FileInputStream serviceAccount = new FileInputStream(System.getProperty("user.dir")+"/serviceaccount.json");

        storageOptions = StorageOptions
                .newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream
                        (serviceAccount))
                .build();
    }


    public void uploadFile(TextFile textFile) throws Exception {
        if (!Arrays.stream(downloadAllFiles()).anyMatch(e -> e.getName().equals(textFile.getName()))){
            log.debug("bucket name====" + bucketName);
            for (FileVersion version: textFile.getVersions()) {
                String objectName = generateFileName(textFile, version);

                Map<String, String> metadata = new HashMap<>();
                String time = LocalDateTime.now().toString();
                metadata.put("createdAt", time);
                metadata.put("lastUpdatedAt", time);
                metadata.put("versionId", String.valueOf(version.getVersionId()));
                metadata.put("locked", "false");

                Storage storage = storageOptions.getService();

                createFileInFirebase(objectName, metadata, storage, version.getFileContent());

                log.info("File uploaded to bucket " + bucketName + " as " + objectName);
            }
        } else {
            log.error("Filename already exists");
            throw new Exception("Filename already exists");
        }

    }

    public TextFile[] downloadAllFiles() {
        Storage storage = storageOptions.getService();
        List<TextFile> files = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);
        // List all files in the directory
        Page<Blob> blobs = bucket.list();

        for (Blob b : blobs.iterateAll()) {
            // Download each file
            byte[] fileBytes = b.getContent();
            String fileName = b.getName();
            String fileContent = new String(fileBytes);
            Map<String, String> metadata = b.getMetadata();
            List<FileVersion> versions = new ArrayList<>();
            try {
                TextFile file = files.stream().filter(e -> e.getName().split("-")[0].equals(fileName.split("-")[0])).findFirst().get();
                FileVersion latestVersion = getLatestFileVersion(file);
                file.getVersions().add(new FileVersion(latestVersion.getVersionId()+1, fileContent, metadata.get("lastUpdatedAt")));
                file.setVersionCount(file.getVersionCount()+1);
            } catch (Exception e){
                versions.add(new FileVersion(
                        Integer.parseInt(metadata.get("versionId")),
                        fileContent,
                        metadata.get("lastUpdatedAt")
                ));
                String a = metadata.get("locked");
                files.add(
                        new TextFile(
                                fileName.split("-")[0],
                                metadata.get("createdAt"),
                                versions,
                                1,
                                Boolean.getBoolean(metadata.get("locked"))
                        )
                );
            }
        }
        return files.toArray(new TextFile[files.size()]);

    }

    public TextFile addVersionAndUnlockFile(TextFile textFile, String versionContent){

        String time = LocalDateTime.now().toString();

        int newVersionId = getLatestFileVersion(textFile).getVersionId()+1;

        textFile.getVersions().add(
                new FileVersion(
                        newVersionId,
                        versionContent,
                        time
                )
        );
        textFile.setVersionCount(newVersionId);
        textFile.setLocked(false);

        Storage storage = storageOptions.getService();
        Bucket bucket = storage.get(bucketName);

        Map<String, String> versionMetaData = new HashMap<>();
        versionMetaData.put("createdAt", textFile.getCreatedAt());
        versionMetaData.put("lastUpdatedAt", time);
        versionMetaData.put("versionId", String.valueOf(newVersionId));
        versionMetaData.put("locked", "false");

        createFileInFirebase(generateFileName(textFile, getLatestFileVersion(textFile)), versionMetaData, storage, versionContent);

        Page<Blob> blobs = bucket.list();

        Map<String, String> updateLockMetaData = new HashMap<>();
        updateLockMetaData.put("locked", "false");

        Storage.BlobTargetOption precondition = Storage.BlobTargetOption.generationMatch();

        for (Blob element : blobs.iterateAll()) {
            if (element.getName().split("-")[0].equals(textFile.getName())) {
                element.toBuilder().setMetadata(updateLockMetaData).build().update(precondition);
            }
        }

        return textFile;
    }

    public TextFile lockOrUnlockFile(TextFile textFile, Boolean lock){
        Storage storage = storageOptions.getService();
        Bucket bucket = storage.get(bucketName);

        Page<Blob> blobs = bucket.list();

        Map<String, String> updateLockMetaData = new HashMap<>();
        updateLockMetaData.put("locked", String.valueOf(lock));

        Storage.BlobTargetOption precondition = Storage.BlobTargetOption.generationMatch();

        for (Blob element : blobs.iterateAll()) {
            if (element.getName().split("-")[0].equals(textFile.getName())) {
                element.toBuilder().setMetadata(updateLockMetaData).build().update(precondition);
            }
        }

        textFile.setLocked(lock);

        return textFile;
    }

    public TextFile resetFileToFormerVersion(TextFile textFile, int versionId) {

        String time = LocalDateTime.now().toString();

        FileVersion copyedFileVersion = getVersionByID(textFile,versionId);
        textFile.getVersions().add(
                new FileVersion(
                        getLatestFileVersion(textFile).getVersionId()+1,
                        copyedFileVersion.getFileContent(),
                        time
                )
        );

        textFile.setVersionCount(textFile.getVersionCount()+1);

        FileVersion latestVersion = getLatestFileVersion(textFile);

        String objectName = generateFileName(textFile, latestVersion);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("createdAt", textFile.getCreatedAt());
        metadata.put("lastUpdatedAt", time);
        metadata.put("versionId", String.valueOf(latestVersion.getVersionId()));
        metadata.put("locked", "false");

        Storage storage = storageOptions.getService();

        createFileInFirebase(objectName, metadata, storage, latestVersion.getFileContent());

        return textFile;
    }


    /*------------------------------------------------UTILS----------------------------------------------------------*/

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    public void createFileInFirebase(String fileName, Map<String, String> metadata, Storage storage, String content){
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(metadata).build();

        Blob blob = storage.create(blobInfo, content.getBytes());
    }

    private String generateFileName(TextFile textFile, FileVersion version) {
        return Objects.requireNonNull(textFile.getName()).replace(" ", "_") + "-" + version.getVersionId();
    }


    private FileVersion getLatestFileVersion(TextFile textFile){
        int maxIndex = 0;
        for (int k = 0; k < textFile.getVersions().size(); k++) {
            if (textFile.getVersions().get(k).getVersionId() > maxIndex) {
                maxIndex = k;
            }
        }
        return textFile.getVersions().get(maxIndex);
    }

    private FileVersion getVersionByID(TextFile textFile, int versionId){
        for (FileVersion element : textFile.getVersions()) {
            if (element.getVersionId() == versionId) return element;
        }
        return null;
    }
}
