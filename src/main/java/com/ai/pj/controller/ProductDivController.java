package com.ai.pj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/productDiv")
@Controller
public class ProductDivController {



    @GetMapping("/")
    public String reqProDiv() {
        return "/storage/manage";
    }
}
