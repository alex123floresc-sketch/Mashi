package com.mashi.omnicanal.shared.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class VistaController {

    private static final Map<String, String> NAV_PRODUCTOS = navItem("Productos", "/admin.html");
    private static final Map<String, String> NAV_ANALITICA = navItem("Analitica", "/admin-analitica.html");
    private static final Map<String, String> NAV_COMPROBANTES = navItem("Comprobantes", "/comprobantes.html");

    @GetMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping("/catalogo.html")
    public String catalogo(Model model) {
        return vista(model, "catalogo", "Catalogo", NAV_COMPROBANTES);
    }

    @GetMapping("/pos.html")
    public String pos(Model model) {
        return vista(model, "pos", "Punto de venta", NAV_COMPROBANTES);
    }

    @GetMapping("/admin.html")
    public String admin(Model model) {
        return vista(model, "admin", "Productos", NAV_ANALITICA, NAV_COMPROBANTES);
    }

    @GetMapping("/admin-analitica.html")
    public String adminAnalitica(Model model) {
        return vista(model, "admin-analitica", "Analitica", NAV_PRODUCTOS, NAV_COMPROBANTES);
    }

    @GetMapping("/comprobantes.html")
    public String comprobantes(Model model) {
        return vista(model, "comprobantes", "Comprobantes");
    }

    private String vista(Model model, String nombreVista, String titulo, Map<String, String>... extras) {
        model.addAttribute("tituloPagina", titulo);
        model.addAttribute("navExtras", List.of(extras));
        return nombreVista;
    }

    private static Map<String, String> navItem(String label, String href) {
        return Map.of("label", label, "href", href);
    }
}
