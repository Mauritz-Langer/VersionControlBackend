package com.highsix.versioncontrol.Controller;

import com.highsix.versioncontrol.Model.File;
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
    public ResponseEntity<String> addFile(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("REST request to upload file");
        firebaseStorage.uploadFile(file);
        return new ResponseEntity<>("Successfully Uploaded!", null, HttpStatus.OK);
    }

    /*
    @GetMapping("getAllFiles")
    public File[] getAllFiles() {
        return firebaseStorage.downloadAllFiles();
    }*/


    @PostMapping("addVersion")
    public void addVersion(@RequestBody File file) {

    }

    @GetMapping("getFileReadOnly")
    public File getFileReadOnly(@RequestParam("id") int fileId) {
        return new File();
    }

    @GetMapping("getFileWithLock")
    public File getFileWithLock(@RequestParam("id") int fileId, @RequestParam("password") String password) {
        return new File();
    }

    @PostMapping("lockFile")
    public void lockFile(@RequestBody File file) {
    }

    @PostMapping("unlockFile")
    public void unlockFile(@RequestBody File file) {
    }

    @PostMapping("resetFileToFormerVersion")
    public void resetFileToFormerVersion(@RequestBody File file, @RequestParam("version") int versionId) {
    }

    @PostMapping("compareFiles")
    public void compareFiles(@RequestBody FileVersion[] filesToCompare) {

    }

}
