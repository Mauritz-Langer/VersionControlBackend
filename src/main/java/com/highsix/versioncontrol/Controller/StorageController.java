package com.highsix.versioncontrol.Controller;

import com.highsix.versioncontrol.Model.TextFile;
import com.highsix.versioncontrol.Model.FileVersion;
import com.highsix.versioncontrol.Service.FirebaseStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class StorageController {

    private final Logger log = LoggerFactory.getLogger(StorageController.class);

    @Qualifier("firebaseStorage")
    @Autowired
    private FirebaseStorage firebaseStorage;

    @PostMapping("addFile")
    public TextFile addFile(@RequestBody TextFile textFile) throws IOException {
        log.info("REST request to upload file");
        firebaseStorage.uploadFile(textFile);
        return textFile;
    }


    @GetMapping("getAllFiles")
    public TextFile[] getAllFiles() {
        return firebaseStorage.downloadAllFiles();
    }


    @PostMapping("addVersionAndUnlockFile")
    public TextFile addVersionAndUnlockFile(@RequestBody TextFile file, @RequestParam("versionContent") String versionContent) {
        return new TextFile();
    }

    @PostMapping("lockFile")
    public TextFile lockFile(@RequestBody TextFile file) {
        return new TextFile();
    }

    @PostMapping("unlockFile")
    public TextFile unlockFile(@RequestBody TextFile file) {
        return new TextFile();
    }

    @PostMapping("resetFileToFormerVersion/{version}")
    public TextFile resetFileToFormerVersion(@RequestBody TextFile file, @PathVariable("version") int versionId) {
        return new TextFile();
    }

}
