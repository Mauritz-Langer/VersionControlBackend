package com.highsix.versioncontrol.Controller;

import com.highsix.versioncontrol.Model.TextFile;
import com.highsix.versioncontrol.Service.FirebaseStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class StorageController {

    private final Logger log = LoggerFactory.getLogger(StorageController.class);

    @Qualifier("firebaseStorage")
    @Autowired
    private FirebaseStorage firebaseStorage;

    @PostMapping("addFile")
    public TextFile addFile(@RequestBody TextFile textFile) {
        log.info("'addFile'-Endpoint was triggered");
        log.debug("Content of the 'addFile'-Request: " + textFile);
        try {
            firebaseStorage.uploadFile(textFile);
            log.info("'addFile'-Request was successfully processed");
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return textFile;
    }


    @GetMapping("getAllFiles")
    public TextFile[] getAllFiles() {
        log.info("'getAllFiles'-Endpoint was triggered");
        return firebaseStorage.downloadAllFiles();
    }


    @PostMapping("addVersionAndUnlockFile")
    public TextFile addVersionAndUnlockFile(@RequestBody TextFile file, @RequestParam String versionContent) {
        log.info("'addVersionAndUnlockFile'-Endpoint was triggered");
        log.debug("Content of the 'addVersionAndUnlockFile'-Request: " + file + "and Version Content: " + versionContent);
        return firebaseStorage.addVersionAndUnlockFile(file,versionContent);
    }

    @PostMapping("lockFile")
    public TextFile lockFile(@RequestBody TextFile file) {
        log.info("'lockFile'-Endpoint was triggered");
        log.debug("Content of the 'lockFile'-Request: " + file);
        return firebaseStorage.lockOrUnlockFile(file, true);
    }

    @PostMapping("unlockFile")
    public TextFile unlockFile(@RequestBody TextFile file) {
        log.info("'unlockFile'-Endpoint was triggered");
        log.debug("Content of the 'unlockFile'-Request: " + file);
        return firebaseStorage.lockOrUnlockFile(file, false);
    }

    @PostMapping("resetFileToFormerVersion/{versionId}")
    public TextFile resetFileToFormerVersion(@RequestBody TextFile file, @PathVariable int versionId) {
        log.info("'resetFileToFormerVersion'-Endpoint was triggered");
        log.debug("Content of the 'resetFileToFormerVersion'-Request: " + file + "and the Version ID: " + versionId);
        return firebaseStorage.resetFileToFormerVersion(file, versionId);
    }

}
