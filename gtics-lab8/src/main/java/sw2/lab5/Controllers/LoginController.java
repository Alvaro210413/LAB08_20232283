package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/redirectByRol")
    public String redirectByRole(Authentication auth, HttpSession httpSession) {
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }
        Usuario usuario = usuarioRepository.findByEmail(auth.getName());
        httpSession.setAttribute("usuario", usuario);
        httpSession.setAttribute("rol", rol);

        if (rol.equals("admin")) {
            return "redirect:/post";
        } else if (rol.equals("user")) {
            return "redirect:/post";
        }
        return "redirect:/";
    }
}
