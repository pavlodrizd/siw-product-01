package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CommentService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    @Autowired private UserService userService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private CommentService commentService;

    /**
     * Mostra la pagina del profilo personale dell'utente loggato.
     * Gestisce sia la vista per l'utente normale che per l'admin.
     */
    @GetMapping("/profile")
    public String showProfile(Model model, Authentication auth) {
        User user = userService.getLoggedUser(auth);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // Controlla se l'utente è un admin per mostrare i commenti corretti
        boolean isAdmin = auth.getAuthorities().stream()
                              .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            model.addAttribute("allComments", commentService.findAll());
        } else {
            model.addAttribute("userComments", commentService.findByAuthor(user));
        }
        
        return "profile";
    }

    /**
     * Mostra il form per modificare il profilo dell'utente loggato.
     */
    @GetMapping("/profile/edit")
    public String showEditProfileForm(Authentication auth, Model model) {
        User user = userService.getLoggedUser(auth);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("credentials", user.getCredentials());
        return "formEditProfile";
    }

    /**
     * Processa i dati inviati dal form di modifica del profilo.
     * Aggiorna nome, cognome ed email.
     */
    @PostMapping("/profile/edit")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("surname") String surname,
                                @RequestParam("email") String email,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        
        User currentUser = userService.getLoggedUser(auth);
        boolean success = userService.updateUserProfile(currentUser, name, surname, email);

        if (success) {
            redirectAttributes.addFlashAttribute("success_message", "Profilo aggiornato con successo!");
        } else {
            redirectAttributes.addFlashAttribute("error_message", "L'email inserita è già utilizzata da un altro account.");
        }
        
        return "redirect:/profile";
    }

    /**
     * Cancella il profilo dell'utente loggato.
     * Questa azione è irreversibile.
     */
    @PostMapping("/profile/delete")
    public String deleteOwnProfile(Authentication auth, HttpServletRequest request) throws ServletException {
        User me = userService.getLoggedUser(auth);
        if (me == null) {
            return "redirect:/login";
        }

        userService.deleteUser(me.getId());
        request.logout(); // Invalida la sessione
        SecurityContextHolder.clearContext(); // Pulisce il contesto di sicurezza

        return "redirect:/"; // Redirect alla home page dopo il logout
    }
}

