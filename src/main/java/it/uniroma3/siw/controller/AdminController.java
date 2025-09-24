package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CommentService;
import it.uniroma3.siw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@PreAuthorize("hasRole('ADMIN')") // Protegge tutti i metodi di questo controller
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private CommentService commentService;

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard() {
        return "admin/dashboard"; // Richiede un file dashboard.html in templates/admin/
    }

    @GetMapping("/admin/manageUsers")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/manageUsers"; // Richiede un file manageUsers.html in templates/admin/
    }

    @GetMapping("/admin/user/{id}")
    public String showUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id); // Qui usiamo il metodo che ritorna User o null
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("userComments", commentService.findByAuthor(user));
            return "admin/userDetail"; 
        }
        return "redirect:/admin/manageUsers";
    }

    @PostMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/manageUsers";
    }
}