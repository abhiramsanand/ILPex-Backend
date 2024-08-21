package com.ILPex.controller;

import com.ILPex.DTO.UserDTO;
import com.ILPex.DTO.UserPostDTO;
import com.ILPex.response.ResponseHandler;
import com.ILPex.service.UserAuthService;
import com.ILPex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import static com.ILPex.constants.Message.USER_DELETED_SUCCESSFULLY;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/view")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> userList = userService.getUsers();
        return ResponseEntity.ok(userList);
    }
    @PostMapping("/save")
    public ResponseEntity<UserPostDTO> createUser(@Valid @RequestBody UserPostDTO userPostDTO){
        return new ResponseEntity<>(userService.createUser(userPostDTO), HttpStatus.CREATED);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseHandler.responseBuilder(USER_DELETED_SUCCESSFULLY,
                HttpStatus.NO_CONTENT, null);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserDTO userDTO) {
        String result = userAuthService.authenticateAndGetRoleId(userDTO);
        return ResponseEntity.ok(result);
    }
}
