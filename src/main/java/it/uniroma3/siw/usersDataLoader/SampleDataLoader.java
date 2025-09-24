package it.uniroma3.siw.usersDataLoader;

import it.uniroma3.siw.model.Comment;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.Type;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CommentRepository;
import it.uniroma3.siw.repository.CredentialsRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.TypeRepository;
import it.uniroma3.siw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class SampleDataLoader implements CommandLineRunner {

    @Autowired private CredentialsRepository credentialsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private TypeRepository typeRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CommentRepository commentRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Pulisce i dati esistenti
        commentRepository.deleteAll();
        productRepository.deleteAll();
        typeRepository.deleteAll();
        credentialsRepository.deleteAll();
        userRepository.deleteAll();

        // 1. CREAZIONE UTENTI
        User adminUser = createUser("Admin", "Sito", "admin@siw.it", "pass", Credentials.ADMIN_ROLE);
        User marioRossi = createUser("Mario", "Rossi", "user@siw.it", "pass", Credentials.DEFAULT_ROLE);
        User lauraBianchi = createUser("Laura", "Bianchi", "user1@siw.it", "pass", Credentials.DEFAULT_ROLE);
        
        // 2. CREAZIONE TIPOLOGIE
        Type libri = createType("Libri");
        Type giardinaggio = createType("Giardinaggio");
        Type elettronica = createType("Elettronica");

        // 3. CREAZIONE PRODOTTI IN MEMORIA CON DESCRIZIONI AMPLIATE
        Product lotr = createProduct("Il Signore degli Anelli", 25.50f, "Un classico della letteratura fantasy che ha definito il genere. Segui il viaggio di Frodo per distruggere l'Unico Anello.", libri, "signoreDegliAnelli.jpg");
        Product hobbit = createProduct("Lo Hobbit", 22.00f, "Il prequel de Il Signore degli Anelli, un'avventura più leggera ma altrettanto affascinante nel cuore della Terra di Mezzo.", libri, "loHobbit.jpg");
        Product silmarillion = createProduct("Il Silmarillion", 24.00f, "La complessa e affascinante mitologia della Terra di Mezzo, dalle origini del mondo alle grandi storie della Prima Era.", libri, "ilSilmarillion.jpg");
        Product orwell1984 = createProduct("1984", 12.00f, "Un romanzo distopico che fa riflettere. Una visione potente e inquietante di un futuro totalitario.", libri, "1984.jpg");
        Product fattoria = createProduct("La fattoria degli animali", 10.50f, "Una satira allegorica del totalitarismo e della corruzione del potere, raccontata attraverso gli animali di una fattoria.", libri, "laFattoriaDegliAnimali.jpg");
        Product guidaGalattica = createProduct("Guida Galattica per Autostoppisti", 15.75f, "Un capolavoro di fantascienza umoristica che esplora l'universo con ironia e genialità. Non farti prendere dal panico!", libri, "guidaGalatticaPerAutostoppisti.jpg");
        Product setGiardinaggio = createProduct("Set di Attrezzi da Giardino", 35.00f, "Un set completo per ogni appassionato di giardinaggio. Include paletta, rastrello e forbici in acciaio inossidabile.", giardinaggio, "setGiardinaggio.jpg");
        Product annaffiatoio = createProduct("Annaffiatoio in Metallo 5L", 19.99f, "Robusto e dal design classico, perfetto per le tue piante da interno ed esterno. Capienza di 5 litri.", giardinaggio, "annaffiatoioMetallo5L.jpg");
        Product cuffieWireless = createProduct("Cuffie Wireless con Cancellazione del Rumore", 99.90f, "Immergiti nella tua musica senza distrazioni. Fino a 30 ore di autonomia e audio ad alta fedeltà.", elettronica, "cuffieWireless.jpg");
        Product smartwatch = createProduct("Smartwatch Fitness Tracker", 59.50f, "Monitora la tua attività fisica, il sonno e ricevi le notifiche direttamente al polso. Resistente all'acqua.", elettronica, "smartwatch.jpg");
        Product tastieraMeccanica = createProduct("Tastiera Meccanica Retroilluminata", 75.00f, "Perfetta per gaming e scrittura. Switch meccanici per un feedback tattile preciso e retroilluminazione RGB personalizzabile.", elettronica, "tastieraMeccanica.jpg");
        
        productRepository.saveAll(List.of(lotr, hobbit, silmarillion, orwell1984, fattoria, guidaGalattica, setGiardinaggio, annaffiatoio, cuffieWireless, smartwatch, tastieraMeccanica));

        // 4. CREAZIONE RELAZIONI TRA PRODOTTI SIMILI
        addSimilar(lotr, hobbit);
        addSimilar(lotr, silmarillion);
        addSimilar(orwell1984, fattoria);
        addSimilar(setGiardinaggio, annaffiatoio);
        
        // 5. CREAZIONE COMMENTI
        createComment("Capolavoro assoluto", "Una lettura imprescindibile per ogni amante del fantasy. Il mondo creato da Tolkien è incredibile.", marioRossi, lotr);
        createComment("Fantastico!", "Ho letto questo libro tre volte e ogni volta scopro nuovi dettagli. Consigliatissimo!", lauraBianchi, lotr);
        createComment("Divertente e geniale", "Un'avventura più leggera ma non meno affascinante. Perfetto per iniziare a esplorare la Terra di Mezzo.", marioRossi, hobbit);
        createComment("Inquietante e attuale", "Un libro che ti rimane dentro. Mette in guardia sui pericoli del totalitarismo in modo magistrale.", lauraBianchi, orwell1984);
        createComment("Ottimo prodotto", "Le cuffie isolano perfettamente dai rumori esterni e la qualità audio è eccellente per il prezzo.", marioRossi, cuffieWireless);
        createComment("Indispensabile!", "Uso questo set da mesi e gli attrezzi sono ancora come nuovi. Ottima qualità costruttiva.", lauraBianchi, setGiardinaggio);

        // MODIFICA: Salviamo i prodotti che hanno ricevuto nuovi commenti.
        // Grazie a CascadeType.ALL, questo salverà automaticamente anche i commenti.
        productRepository.saveAll(List.of(lotr, hobbit, orwell1984, cuffieWireless, setGiardinaggio));

        System.out.println("✅ Sample data loaded successfully.");
    }
    
    // Metodi helper
    
    private User createUser(String name, String surname, String email, String password, String role) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        Credentials credentials = new Credentials();
        credentials.setEmail(email);
        credentials.setPassword(passwordEncoder.encode(password));
        credentials.setRole(role);
        credentials.setUser(user);
        credentialsRepository.save(credentials);
        return user;
    }

    private Type createType(String name) {
        Type type = new Type();
        type.setName(name);
        return typeRepository.save(type);
    }

    private Product createProduct(String name, Float price, String description, Type type, String imagePath) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setType(type);
        product.setImagePath(imagePath);
        return product;
    }

    private void addSimilar(Product p1, Product p2) {
        if (p1 != null && p2 != null) {
            p1.getSimilarProducts().add(p2);
            p2.getSimilarProducts().add(p1);
        }
    }

    // MODIFICA: Questo metodo ora aggiorna anche la lista interna del prodotto
    private void createComment(String title, String text, User author, Product product) {
        Comment comment = new Comment();
        comment.setTitle(title);
        comment.setText(text);
        comment.setAuthor(author);
        comment.setProduct(product);
        // La riga chiave: aggiorniamo anche la lista del prodotto!
        product.getComments().add(comment);
    }
}

