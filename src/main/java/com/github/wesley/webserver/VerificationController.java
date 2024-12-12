package com.github.wesley.webserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class VerificationController {
    private static final String PATH_POST_VERIFY_USER = "/verify-user";

    private final VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @RequestMapping(path = PATH_POST_VERIFY_USER, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    void verifyUser(@RequestBody String verificationCode) {
        verificationService.verifyUser(verificationCode);
    }
}
