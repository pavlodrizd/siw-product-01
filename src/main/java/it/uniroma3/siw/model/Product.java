package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.util.ArrayList; // <-- IMPORT NECESSARIO
import java.util.HashSet;   // <-- IMPORT NECESSARIO
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private Float price;

    @Column(length = 1000)
    private String description;
    
    private String imagePath;

    // RELAZIONI
    @ManyToOne
    private Type type;

    // CORREZIONE QUI: Aggiunta l'inizializzazione
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    // CORREZIONE QUI: Aggiunta l'inizializzazione
    @ManyToMany
    private Set<Product> similarProducts = new HashSet<>();


    // COSTRUTTORI, GETTERS E SETTERS
    public Product() {
    }

    // Getters e Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Float getPrice() { return price; }
    public void setPrice(Float price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    public Set<Product> getSimilarProducts() { return similarProducts; }
    public void setSimilarProducts(Set<Product> similarProducts) { this.similarProducts = similarProducts; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }


    // METODI EQUALS E HASHCODE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}