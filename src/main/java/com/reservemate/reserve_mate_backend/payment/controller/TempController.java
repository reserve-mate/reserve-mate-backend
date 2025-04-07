package com.reservemate.reserve_mate_backend.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TempController {

    @RequestMapping(value = "/")
    public String getMethodName() {
        return "index";
    }

    @RequestMapping(value = "/toss/success", method = RequestMethod.GET)
    public String requestMethodName() {
        return new String();
    }

}
