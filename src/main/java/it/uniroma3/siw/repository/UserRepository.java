package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.User;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
	
    /**
     * Trova un utente tramite il suo nome e cognome.
     * È una query più robusta rispetto alla ricerca per solo nome.
     * @param name il nome dell'utente
     * @param surname il cognome dell'utente
     * @return un Optional contenente l'utente se trovato, altrimenti vuoto
     */
    public Optional<User> findByNameAndSurname(String name, String surname);

}