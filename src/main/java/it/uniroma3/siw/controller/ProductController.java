package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Comment; // Assicurati che questo import ci sia
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Type;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.TypeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List; // Assicurati che questo import ci sia

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private TypeService typeService;

    @GetMapping("/products")
    public String getProducts(Model model) {
        model.addAttribute("products", this.productService.findAll());
        return "products";
    }
    
    @Transactional
    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable("id") Long id, Model model) {
        Product product = this.productService.findById(id);
        if (product != null) {
            model.addAttribute("product", product);

            // Prepara la lista per il dropdown dei prodotti simili
            List<Product> potentialSimilarProducts = this.productService.findAll();
            
            // 1. Rimuovi il prodotto corrente dalla lista
            potentialSimilarProducts.remove(product);
            
            // 2. Rimuovi tutti i prodotti che sono GIÀ simili
            potentialSimilarProducts.removeAll(product.getSimilarProducts());
            
            model.addAttribute("allProducts", potentialSimilarProducts);
            
            return "product";
        } else {
            return "redirect:/products";
        }
    }
    
    @GetMapping("/search")
    public String searchProducts(@RequestParam(value = "query", required = false) String query,
                                 @RequestParam(value = "typeId", required = false) Long typeId,
                                 Model model) {
        
        // Questa parte è corretta e recupera i prodotti filtrati
        model.addAttribute("products", productService.search(query, typeId));
        model.addAttribute("query", query);

        // ===================================================================
        // INSERISCI QUESTO BLOCCO DI LOGICA PER IL TITOLO DINAMICO
        // ===================================================================
        StringBuilder pageTitle = new StringBuilder("Risultati Ricerca");

        // Aggiunge il testo della ricerca, se presente
        if (query != null && !query.trim().isEmpty()) {
            pageTitle.append(" per '").append(query).append("'");
        }

        // Aggiunge il nome della tipologia, se selezionata
        if (typeId != null) {
            typeService.findById(typeId).ifPresent(type -> 
                pageTitle.append(" in '").append(type.getName()).append("'")
            );
        }
        
        model.addAttribute("pageTitle", pageTitle.toString());
        // ===================================================================
        
        return "products";
    }

    // METODI ADMIN

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/addProduct")
    public String formNewProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("types", this.typeService.findAll());
        return "admin/formNewProduct";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/addProduct")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                // <-- 1. AGGIUNGI QUESTO NUOVO PARAMETRO
                                @RequestParam(name = "newTypeName", required = false) String newTypeName,
                                Model model) {

        // <-- 2. AGGIUNGI QUESTO BLOCCO DI LOGICA ALL'INIZIO DEL METODO
        if (newTypeName != null && !newTypeName.trim().isEmpty()) {
            // Se l'admin ha inserito il nome di una nuova tipologia, creala o trovala
            Type newType = typeService.findOrCreateByName(newTypeName.trim());
            product.setType(newType);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", this.typeService.findAll());
            return "admin/formNewProduct";
        }
        try {
            Product savedProduct = this.productService.save(product, imageFile);
            if (savedProduct != null) {
                return "redirect:/product/" + savedProduct.getId();
            } else {
                model.addAttribute("messaggioErrore", "Un prodotto con questo nome esiste già.");
                model.addAttribute("types", this.typeService.findAll());
                return "admin/formNewProduct";
            }
        } catch (IOException e) {
            model.addAttribute("messaggioErrore", "Errore nel salvataggio dell'immagine.");
            model.addAttribute("types", this.typeService.findAll());
            return "admin/formNewProduct";
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/manageProducts")
    public String manageProducts(Model model) {
        model.addAttribute("products", this.productService.findAll());
        return "admin/manageProducts";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/editProduct/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = this.productService.findById(id);
        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("types", this.typeService.findAll());
            return "admin/formEditProduct";
        }
        return "redirect:/admin/manageProducts";
    }

    /**
     * Processa i dati inviati dal form di modifica.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/editProduct/{id}")
    public String updateProduct(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                // Aggiungi il nuovo parametro per la tipologia
                                @RequestParam(name = "newTypeName", required = false) String newTypeName,
                                Model model) {

        // Aggiungi la logica per gestire la creazione di una nuova tipologia
        if (newTypeName != null && !newTypeName.trim().isEmpty()) {
            Type newType = typeService.findOrCreateByName(newTypeName.trim());
            product.setType(newType);
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("types", this.typeService.findAll());
            return "admin/formEditProduct";
        }
        try {
            this.productService.update(id, product, imageFile);
        } catch (IOException e) {
            model.addAttribute("messaggioErrore", "Errore nel salvataggio della nuova immagine.");
            model.addAttribute("types", this.typeService.findAll());
            return "admin/formEditProduct";
        }
        return "redirect:/admin/manageProducts";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") Long id, 
                                @RequestParam(name = "source", required = false) String source) {
        this.productService.deleteById(id);
        
        // Controlla il valore del parametro 'source' per decidere dove reindirizzare
        if ("catalog".equals(source)) {
            return "redirect:/products";
        }
        
        // Se 'source' non è 'catalog' (o è nullo), torna alla dashboard come prima
        return "redirect:/admin/manageProducts";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/addSimilarProduct") // URL Semplificato
    public String addSimilarProduct(@RequestParam("productId") Long productId, 
                                    @RequestParam("similarProductId") Long similarProductId) {
        productService.addSimilarProduct(productId, similarProductId);
        return "redirect:/product/" + productId;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/product/{productId}/removeSimilar/{similarProductId}")
    public String removeSimilarProduct(@PathVariable("productId") Long productId,
                                       @PathVariable("similarProductId") Long similarProductId) {
        productService.removeSimilarProduct(productId, similarProductId);
        return "redirect:/product/" + productId;
    }
}