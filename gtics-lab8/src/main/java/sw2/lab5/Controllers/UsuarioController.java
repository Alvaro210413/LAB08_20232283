package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.PostRepository;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PostRepository postRepository;

    private void copiarDatos(Usuario origen, Usuario destino) {
        destino.setFirstname(origen.getFirstname().trim());
        destino.setLastname(origen.getLastname().trim());
        destino.setIntro(origen.getIntro());
        destino.setProfile(origen.getProfile());
    }

    @GetMapping("")
    public String miPerfil(Model model, HttpSession session) {
        Usuario sesion = (Usuario) session.getAttribute("usuario");
        Usuario usuario = usuarioRepository.findById(sesion.getIdUser()).orElse(sesion);
        model.addAttribute("usuario", usuario);
        model.addAttribute("cantidadPosts", postRepository.countByUser_IdUser(usuario.getIdUser()));
        model.addAttribute("accion", "/user/guardar");
        return "user/form";
    }

    @PostMapping("/guardar")
    public String guardarPerfil(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult,
                                Model model, HttpSession session, RedirectAttributes attr) {
        Usuario sesion = (Usuario) session.getAttribute("usuario");
        Usuario actual = usuarioRepository.findById(sesion.getIdUser()).orElse(null);
        if (actual == null) {
            return "redirect:/post";
        }
        if (bindingResult.hasErrors()) {
            usuario.setIdUser(actual.getIdUser());
            usuario.setEmail(actual.getEmail());
            usuario.setRol(actual.getRol());
            model.addAttribute("cantidadPosts", postRepository.countByUser_IdUser(actual.getIdUser()));
            model.addAttribute("accion", "/user/guardar");
            return "user/form";
        }
        copiarDatos(usuario, actual);
        usuarioRepository.save(actual);
        session.setAttribute("usuario", actual);
        attr.addFlashAttribute("msg", "Perfil actualizado exitosamente");
        return "redirect:/user";
    }

    @GetMapping("/list")
    public String listaUsuarios(Model model) {
        model.addAttribute("listausuarios", usuarioRepository.findAllByOrderByIdUserAsc());
        return "user/list";
    }

    @GetMapping("/editar")
    public String editar(Model model, @RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) {
            attr.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/user/list";
        }
        model.addAttribute("usuario", opt.get());
        model.addAttribute("cantidadPosts", postRepository.countByUser_IdUser(id));
        model.addAttribute("accion", "/user/actualizar");
        return "user/form";
    }

    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult,
                             Model model, HttpSession session, RedirectAttributes attr) {
        Optional<Usuario> opt = usuarioRepository.findById(usuario.getIdUser());
        if (opt.isEmpty()) {
            attr.addFlashAttribute("error", "El usuario no existe");
            return "redirect:/user/list";
        }
        Usuario actual = opt.get();
        if (bindingResult.hasErrors()) {
            usuario.setEmail(actual.getEmail());
            usuario.setRol(actual.getRol());
            model.addAttribute("cantidadPosts", postRepository.countByUser_IdUser(actual.getIdUser()));
            model.addAttribute("accion", "/user/actualizar");
            return "user/form";
        }
        copiarDatos(usuario, actual);
        usuarioRepository.save(actual);

        Usuario sesion = (Usuario) session.getAttribute("usuario");
        if (sesion.getIdUser() == actual.getIdUser()) {
            session.setAttribute("usuario", actual);
        }
        attr.addFlashAttribute("msg", "Usuario actualizado exitosamente");
        return "redirect:/user/list";
    }

    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario sesion = (Usuario) session.getAttribute("usuario");
        if (sesion.getIdUser() == id) {
            attr.addFlashAttribute("error", "No puedes desactivar tu propia cuenta");
            return "redirect:/user/list";
        }
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isPresent()) {
            Usuario usuario = opt.get();
            usuario.setActive(!usuario.isActive());
            usuarioRepository.save(usuario);
            attr.addFlashAttribute("msg", usuario.isActive() ? "Usuario activado" : "Usuario desactivado");
        } else {
            attr.addFlashAttribute("error", "El usuario no existe");
        }
        return "redirect:/user/list";
    }
}
