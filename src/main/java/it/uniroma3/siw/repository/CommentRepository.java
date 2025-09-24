package it.uniroma3.siw.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.model.Product; // Importa Product
import it.uniroma3.siw.model.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Trova tutti i commenti per un determinato prodotto
    List<Comment> findByProduct(Product product);

    // Trova tutti i commenti scritti da un determinato utente (autore)
    List<Comment> findByAuthor(User author);

    // Cancella tutti i commenti associati a un prodotto
    void deleteByProduct(Product product);

    // Cancella tutti i commenti scritti da un utente (autore)
    void deleteByAuthor(User author);
}