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

        log.info("Startup the Google Cloud Storage Services");

        FileInputStream serviceAccount = new FileInputStream(System.getProperty("user.dir")+"/serviceaccount.json");

        storageOptions = StorageOptions
                .newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream
                        (serviceAccount))
                .build();

        log.info("Finished successfully the startup of the Google Cloud Storage Services");
    }


    public void uploadFile(TextFile textFile) throws Exception {
        log.debug("'uploadFile'-Methode was triggered with content: " + textFile);
        if (!Arrays.stream(downloadAllFiles()).anyMatch(e -> e.getName().equals(textFile.getName()))){
            log.info("Found no file that matches the input textFile");
            log.debug("bucket name====" + bucketName);
            for (FileVersion version: textFile.getVersions()) {
                String objectName = generateFileName(textFile, version);
                log.info("New Filename was generated: " + objectName);

                Map<String, String> metadata = new HashMap<>();
                String time = LocalDateTime.now().toString();
                metadata.put("createdAt", time);
                log.debug("createdAt: " + time);
                metadata.put("lastUpdatedAt", time);
                log.debug("lastUpdatedAt: " + time);
                metadata.put("versionId", String.valueOf(version.getVersionId()));
                log.debug("versionId: " + version.getVersionId());
                metadata.put("locked", "false");
                log.debug("locked: false");

                Storage storage = storageOptions.getService();
                log.debug("Storage was initialized");

                log.info("Calling 'createFileInFirebase'-Methode with file-content: " + version.getFileContent());
                createFileInFirebase(objectName, metadata, storage, version.getFileContent());
                log.info("Finished 'createFileInFirebase'-Methode successfully");

                log.info("File uploaded to bucket " + bucketName + " as " + objectName);
            }
        } else {
            log.error("Filename already exists");
            throw new Exception("Filename already exists");
        }

    }

    public TextFile[] downloadAllFiles() {
        log.debug("'downloadAllFiles'-Methode was called");
        Storage storage = storageOptions.getService();
        List<TextFile> files = new ArrayList<>();
        Bucket bucket = storage.get(bucketName);
        log.debug("Bucket was defined: "+ bucketName);
        // List all files in the directory
        Page<Blob> blobs = bucket.list();
        log.debug("Blob with all entrys was created: " + blobs.getValues());

        for (Blob b : blobs.iterateAll()) {
            // Download each file
            byte[] fileBytes = b.getContent();
            log.debug("Content: " + Arrays.toString(b.getContent()));
            String fileName = b.getName();
            log.debug("filename: " + b.getName());
            String fileContent = new String(fileBytes);
            Map<String, String> metadata = b.getMetadata();
            List<FileVersion> versions = new ArrayList<>();
            try {
                TextFile file = files.stream().filter(e -> e.getName().split("€")[0].equals(fileName.split("€")[0])).findFirst().get();
                log.info("Found file with same Filename");
                FileVersion latestVersion = getLatestFileVersion(file);
                log.debug("Found latest Version: " + latestVersion);
                file.getVersions().add(new FileVersion(latestVersion.getVersionId()+1, fileContent, metadata.get("lastUpdatedAt")));
                file.setVersionCount(file.getVersionCount()+1);
                log.info("New Version was created");
            } catch (Exception e){
                log.info("No File with the Filename was foung");
                versions.add(new FileVersion(
                        Integer.parseInt(metadata.get("versionId")),
                        fileContent,
                        metadata.get("lastUpdatedAt")
                ));
                files.add(
                        new TextFile(
                                fileName.split("€")[0],
                                metadata.get("createdAt"),
                                versions,
                                1,
                                Boolean.parseBoolean(metadata.get("locked"))
                        )
                );
                log.info("Created new File with one version");
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

        log.info("New Version was created");

        Storage storage = storageOptions.getService();
        Bucket bucket = storage.get(bucketName);

        Map<String, String> versionMetaData = new HashMap<>();
        versionMetaData.put("createdAt", textFile.getCreatedAt());
        log.debug("createAt: " + textFile.getCreatedAt());
        versionMetaData.put("lastUpdatedAt", time);
        log.debug("lastUpdatedAt: " + time);
        versionMetaData.put("versionId", String.valueOf(newVersionId));
        log.debug("versionId: " + newVersionId);
        versionMetaData.put("locked", "false");
        log.debug("locked: false");

        log.info("Calling 'createFileInFirebase'-Methode with file-content: " + versionContent);
        createFileInFirebase(generateFileName(textFile, getLatestFileVersion(textFile)), versionMetaData, storage, versionContent);
        log.info("'createFileInFirebase'-Methode was processed successfully");

        Page<Blob> blobs = bucket.list();

        Map<String, String> updateLockMetaData = new HashMap<>();
        updateLockMetaData.put("locked", "false");
        log.info("locked: false");

        Storage.BlobTargetOption precondition = Storage.BlobTargetOption.generationMatch();

        for (Blob element : blobs.iterateAll()) {
            if (element.getName().split("€")[0].equals(textFile.getName())) {
                element.toBuilder().setMetadata(updateLockMetaData).build().update(precondition);
            }
        }
        log.info("Switch locked to false of the file");

        return textFile;
    }

    public TextFile lockOrUnlockFile(TextFile textFile, Boolean lock){
        Storage storage = storageOptions.getService();
        Bucket bucket = storage.get(bucketName);

        Page<Blob> blobs = bucket.list();
        log.debug("Blobs: " + blobs.getValues());

        Map<String, String> updateLockMetaData = new HashMap<>();
        updateLockMetaData.put("locked", String.valueOf(lock));
        log.debug("locked: " + lock);

        Storage.BlobTargetOption precondition = Storage.BlobTargetOption.generationMatch();

        for (Blob element : blobs.iterateAll()) {
            if (element.getName().split("€")[0].equals(textFile.getName())) {
                log.debug("Version with right name was found");
                element.toBuilder().setMetadata(updateLockMetaData).build().update(precondition);
                log.debug("Lock status of Version was updated");
            }
        }

        textFile.setLocked(lock);

        log.info("File lock status was updated");

        return textFile;
    }

    public TextFile resetFileToFormerVersion(TextFile textFile, int versionId) {

        String time = LocalDateTime.now().toString();

        FileVersion copyedFileVersion = getVersionByID(textFile,versionId);
        log.debug("Version to copy was found: " + copyedFileVersion);
        textFile.getVersions().add(
                new FileVersion(
                        getLatestFileVersion(textFile).getVersionId()+1,
                        copyedFileVersion.getFileContent(),
                        time
                )
        );
        log.info("File was reset to former version");

        textFile.setVersionCount(textFile.getVersionCount()+1);

        FileVersion latestVersion = getLatestFileVersion(textFile);

        String objectName = generateFileName(textFile, latestVersion);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("createdAt", textFile.getCreatedAt());
        log.debug("createAt: " + textFile.getCreatedAt());
        metadata.put("lastUpdatedAt", time);
        log.debug("lastUpdatedAt: " + time);
        metadata.put("versionId", String.valueOf(latestVersion.getVersionId()));
        log.debug("versionId: " + latestVersion.getVersionId());
        metadata.put("locked", "false");
        log.debug("locked: false");

        Storage storage = storageOptions.getService();

        log.info("Calling 'createFileInFirebase'-Methode with file-content: " + latestVersion.getFileContent());
        createFileInFirebase(generateFileName(textFile, getLatestFileVersion(textFile)), metadata, storage, latestVersion.getFileContent());
        log.info("'createFileInFirebase'-Methode was processed successfully");

        return textFile;
    }


    /*------------------------------------------------UTILS----------------------------------------------------------*/

    public void createFileInFirebase(String fileName, Map<String, String> metadata, Storage storage, String content){
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setMetadata(metadata).build();

        storage.create(blobInfo, content.getBytes());
        log.info("Created File in Firebase");
    }

    private String generateFileName(TextFile textFile, FileVersion version) {
        return Objects.requireNonNull(textFile.getName()).replace(" ", "_") + "€" + version.getVersionId();
    }


    private FileVersion getLatestFileVersion(TextFile textFile){
        int maxIndex = 0;
        for (int k = 0; k < textFile.getVersions().size(); k++) {
            if (textFile.getVersions().get(k).getVersionId() > textFile.getVersions().get(maxIndex).getVersionId()) {
                maxIndex = k;
                log.debug("maxFileVersionIndex = " + maxIndex);
            }
        }
        return textFile.getVersions().get(maxIndex);
    }

    private FileVersion getVersionByID(TextFile textFile, int versionId){
        for (FileVersion element : textFile.getVersions()) {
            if (element.getVersionId() == versionId){
                log.debug("Version was found by ID");
                return element;
            }
        }
        log.error("No version was found");
        return null;
    }
}
