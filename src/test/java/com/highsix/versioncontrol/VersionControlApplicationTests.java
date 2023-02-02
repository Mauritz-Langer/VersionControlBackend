package com.highsix.versioncontrol;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Page;
import com.google.cloud.AsyncPageImpl;
import com.google.cloud.PageImpl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.highsix.versioncontrol.Controller.StorageController;
import com.highsix.versioncontrol.Model.TextFile;
import com.highsix.versioncontrol.Service.FirebaseStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class VersionControlApplicationTests {

    @Spy @InjectMocks
    StorageController storageController;

    @Spy @InjectMocks
    FirebaseStorage firebaseStorage;



    @Test
    void addFile() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String newTextFile = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": false\n" +
                "}";
        TextFile textFile = mapper.readValue(newTextFile, TextFile.class);

        String correctResultString = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": false\n" +
                "}";
        TextFile correctResult = mapper.readValue(correctResultString, TextFile.class);

        String downloadedFiles = "[\n" +
                "  {\n" +
                "    \"name\": \"Datei_1.txt\",\n" +
                "    \"createdAt\": \"2023-01-21T08:52:54.647037500\",\n" +
                "    \"versions\": [\n" +
                "      {\n" +
                "        \"versionId\": 1,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-21T08:52:54.647037500\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 2,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-21T17:14:36.416\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 3,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-29T16:51:47.937\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 4,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-29T16:52:11.055\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 5,\n" +
                "        \"fileContent\": \"Das ist ein Test für das Editieren\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-31T17:56:18.897\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"versionCount\": 5,\n" +
                "    \"locked\": false\n" +
                "  }\n" +
                "]";
        TextFile[] textFiles = mapper.readValue(downloadedFiles, TextFile[].class);


        Mockito.doReturn(textFiles).when(firebaseStorage).downloadAllFiles();
        Mockito.doNothing().when(firebaseStorage).createFileInFirebase(Mockito.anyString(), Mockito.any(HashMap.class), Mockito.anyString());

        TextFile result = storageController.addFile(textFile);

        Assertions.assertTrue(correctResult.like(result));
    }

    @Test
    void getAllFiles() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String downloadedFiles = "[\n" +
                "  {\n" +
                "    \"name\": \"Datei_1.txt\",\n" +
                "    \"createdAt\": \"2023-01-21T08:52:54.647037500\",\n" +
                "    \"versions\": [\n" +
                "      {\n" +
                "        \"versionId\": 1,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-21T08:52:54.647037500\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 2,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-21T17:14:36.416\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 3,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-29T16:51:47.937\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 4,\n" +
                "        \"fileContent\": \"\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-29T16:52:11.055\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"versionId\": 5,\n" +
                "        \"fileContent\": \"Das ist ein Test für das Editieren\",\n" +
                "        \"lastUpdatedAt\": \"2023-01-31T17:56:18.897\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"versionCount\": 5,\n" +
                "    \"locked\": false\n" +
                "  }\n" +
                "]";
        TextFile[] textFiles = mapper.readValue(downloadedFiles, TextFile[].class);

        Mockito.doReturn(textFiles).when(firebaseStorage).downloadAllFiles();

        TextFile[] result = storageController.getAllFiles();

        int i = 0;
        for (TextFile file : textFiles) {
            Assertions.assertTrue(file.like(result[i]));
            i++;
        }

    }

    @Test
    void addVersionAndUnlockFile() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String newTextFile = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": true\n" +
                "}";
        TextFile textFile = mapper.readValue(newTextFile, TextFile.class);

        String correctResultString = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"versionId\": 2,\n" +
                "      \"fileContent\": \"Version2\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 2,\n" +
                "  \"locked\": false\n" +
                "}";
        TextFile correctResult = mapper.readValue(correctResultString, TextFile.class);

        String versionContent = "Version2";

        Mockito.doNothing().when(firebaseStorage).createFileInFirebase(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Mockito.doNothing().when(firebaseStorage).updateLockStatus(Mockito.any(), Mockito.any(TextFile.class));

        TextFile result = storageController.addVersionAndUnlockFile(textFile,versionContent);

        Assertions.assertTrue(correctResult.like(result));
    }

    @Test
    void lockFile() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String newTextFile = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": false\n" +
                "}";
        TextFile textFile = mapper.readValue(newTextFile, TextFile.class);

        String correctResultString = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": true\n" +
                "}";
        TextFile correctResult = mapper.readValue(correctResultString, TextFile.class);

        Mockito.doNothing().when(firebaseStorage).updateLockStatus(Mockito.any(), Mockito.any(TextFile.class));

        TextFile result = storageController.lockFile(textFile);

        Assertions.assertTrue(correctResult.like(result));
    }

    @Test
    void unlockFile() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String newTextFile = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": true\n" +
                "}";
        TextFile textFile = mapper.readValue(newTextFile, TextFile.class);

        String correctResultString = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": false\n" +
                "}";
        TextFile correctResult = mapper.readValue(correctResultString, TextFile.class);

        Mockito.doNothing().when(firebaseStorage).updateLockStatus(Mockito.any(), Mockito.any(TextFile.class));

        TextFile result = storageController.unlockFile(textFile);

        Assertions.assertTrue(correctResult.like(result));
    }

    @Test
    void resetFileToFormerVersion() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String newTextFile = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 1,\n" +
                "  \"locked\": false\n" +
                "}";
        TextFile textFile = mapper.readValue(newTextFile, TextFile.class);

        String correctResultString = "{\n" +
                "  \"name\": \"File1\",\n" +
                "  \"createdAt\": \"heute\",\n" +
                "  \"versions\": [\n" +
                "    {\n" +
                "      \"versionId\": 1,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"versionId\": 2,\n" +
                "      \"fileContent\": \"Version1\",\n" +
                "      \"lastUpdatedAt\": \"heute\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"versionCount\": 2,\n" +
                "  \"locked\": false\n" +
                "}";
        TextFile correctResult = mapper.readValue(correctResultString, TextFile.class);

        Mockito.doNothing().when(firebaseStorage).createFileInFirebase(Mockito.anyString(), Mockito.any(), Mockito.anyString());

        TextFile result = storageController.resetFileToFormerVersion(textFile,1);

        Assertions.assertTrue(correctResult.like(result));
    }

}
