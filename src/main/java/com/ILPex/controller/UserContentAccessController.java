package com.ILPex.controller;

import com.ILPex.DTO.UserContentAccessDTO;
import com.ILPex.service.UserContentAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-content-access")
public class UserContentAccessController {

    @Autowired
    private UserContentAccessService userContentAccessService;

    @PostMapping("/save")
    public void saveUserContentAccess(@RequestBody UserContentAccessDTO userContentAccessDTO) {
        userContentAccessService.saveUserContentAccess(userContentAccessDTO);
    }

    @GetMapping("/all")
    public List<UserContentAccessDTO> getAllUserContentAccess() {
        return userContentAccessService.getAllUserContentAccess();
    }
}
