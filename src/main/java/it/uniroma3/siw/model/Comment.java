package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comment") // Tabella rinominata
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Il titolo non può essere vuoto")
    private String title;

    @NotBlank(message = "Il testo non può essere vuoto")
    @Column(columnDefinition = "TEXT")
    private String text;

    // Campo 'vote' rimosso

    private LocalDateTime creationTimestamp; // Campo per la data di creazione

    @ManyToOne
    private Product product; // Relazione aggiornata da Album a Product

    @ManyToOne
    private User author; // Rinominato da 'user' ad 'author' per chiarezza

    /**
     * Metodo eseguito automaticamente prima del primo salvataggio (persist)
     * per impostare il timestamp di creazione.
     */
    @PrePersist
    protected void onCreate() {
        this.creationTimestamp = LocalDateTime.now();
    }

    // Getter e setter aggiornati

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreationTimestamp() { return creationTimestamp; }
    public void setCreationTimestamp(LocalDateTime creationTimestamp) { this.creationTimestamp = creationTimestamp; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}