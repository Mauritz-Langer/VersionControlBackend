package com.highsix.versioncontrol.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;


@Service
@Component("firebaseStorage")
public class FirebaseStorage {
    private final Logger log = LoggerFactory.getLogger(FirebaseStorage.class);

    private StorageOptions storageOptions;
    private String bucketName = "versionverwaltung.appspot.com";
    private String projectId = "versionverwaltung";

    @PostConstruct
    public void initialize() throws IOException {

        FileInputStream serviceAccount = new FileInputStream("C:/Users/Benyamin/VersionControlBackend/serviceaccount.json");

        storageOptions = StorageOptions
                .newBuilder()
                .setProjectId(projectId)
                .setCredentials(GoogleCredentials.fromStream
                        (serviceAccount))
                .build();


    }


    public String[] uploadFile(MultipartFile multipartFile) throws IOException {
        log.debug("bucket name====" + bucketName);
        File file = convertMultiPartToFile(multipartFile);
        Path filePath = file.toPath();
        String objectName = generateFileName(multipartFile);

        Storage storage = storageOptions.getService();

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Blob blob = storage.create(blobInfo, Files.readAllBytes(filePath));

        log.info("File " + filePath + " uploaded to bucket " + bucketName + " as " + objectName);
        return new String[]{"fileUrl", objectName};
    }


    /*------------------------------------------------UTILS----------------------------------------------------------*/

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }


}