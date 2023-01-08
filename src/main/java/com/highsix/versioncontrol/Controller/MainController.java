package com.highsix.versioncontrol.Controller;

import com.highsix.versioncontrol.Model.File;
import com.highsix.versioncontrol.Model.FileVersion;
import com.highsix.versioncontrol.Service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @PostMapping("addFile")
    public void addFile(@RequestBody File file){
        mainService.addFile();
    }

    @PostMapping("addVersion")
    public void addVersion(@RequestBody File file){
    }

    @GetMapping("getAllFiles")
    public File[] getAllFiles(){
        return new File[]{new File()};
    }

    @GetMapping("getFileReadOnly")
    public File getFileReadOnly(@RequestParam("id") int fileId){
        return new File();
    }

    @GetMapping("getFileWithLock")
    public File getFileWithLock(@RequestParam("id") int fileId, @RequestParam("password") String password){
        return new File();
    }

    @PostMapping("lockFile")
    public void lockFile(@RequestBody File file){
    }

    @PostMapping("unlockFile")
    public void unlockFile(@RequestBody File file){
    }

    @PostMapping("resetFileToFormerVersion")
    public void resetFileToFormerVersion(@RequestBody File file, @RequestParam("version") int versionId){
    }

    @PostMapping("compareFiles")
    public void compareFiles(@RequestBody FileVersion[] filesToCompare){
    }

}
