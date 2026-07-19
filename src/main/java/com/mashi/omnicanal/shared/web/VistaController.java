package com.mashi.omnicanal.shared.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

    @GetMapping("/catalogo.html")
    public String catalogo() {
        return "catalogo";
    }

    @GetMapping("/pos.html")
    public String pos() {
        return "pos";
    }

    @GetMapping("/admin.html")
    public String admin() {
        return "admin";
    }
}
