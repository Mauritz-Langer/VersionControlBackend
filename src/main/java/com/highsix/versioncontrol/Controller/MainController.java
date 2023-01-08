package com.highsix.versioncontrol.Controller;

import com.highsix.versioncontrol.Model.File;
import com.highsix.versioncontrol.Service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @PostMapping("addFile")
    public void addFile(@RequestBody File file){
        mainService.addFile();
    }

}
