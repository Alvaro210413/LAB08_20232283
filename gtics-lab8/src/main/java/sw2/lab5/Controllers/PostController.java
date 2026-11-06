package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.Post;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.PostRepository;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    PostRepository postRepository;

    private boolean esAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("rol"));
    }

    private Usuario usuarioSesion(HttpSession session) {
        return (Usuario) session.getAttribute("usuario");
    }

    private boolean puedeModificar(Post post, HttpSession session) {
        Usuario usuario = usuarioSesion(session);
        if (usuario == null) {
            return false;
        }
        return esAdmin(session) || post.getUser().getIdUser() == usuario.getIdUser();
    }

    private boolean puedeVer(Post post, HttpSession session) {
        return post.isPublished() || puedeModificar(post, session);
    }

    @GetMapping({"", "/", "/list"})
    public String listaPost(Model model, HttpSession session) {
        Usuario usuario = usuarioSesion(session);
        List<Post> lista;
        if (usuario == null) {
            lista = postRepository.findByPublishedTrueOrderByIdPostDesc();
        } else if (esAdmin(session)) {
            lista = postRepository.findAllByOrderByIdPostDesc();
        } else {
            lista = postRepository.listarParaUsuario(usuario.getIdUser());
        }
        model.addAttribute("lista", lista);
        return "post/lista";
    }

    @GetMapping("/ver")
    public String verPost(Model model, @RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Optional<Post> opt = postRepository.findById(id);
        if (opt.isEmpty() || !puedeVer(opt.get(), session)) {
            attr.addFlashAttribute("error", "El post no existe o no tienes permiso para verlo");
            return "redirect:/post";
        }
        model.addAttribute("post", opt.get());
        model.addAttribute("puedeModificar", puedeModificar(opt.get(), session));
        return "post/ver";
    }

    @GetMapping("/create")
    public String nuevoPost(@ModelAttribute("post") Post post) {
        return "post/form";
    }

    @GetMapping("/edit")
    public String editarPost(Model model, @RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Optional<Post> opt = postRepository.findById(id);
        if (opt.isEmpty() || !puedeModificar(opt.get(), session)) {
            attr.addFlashAttribute("error", "No tienes permiso para editar este post");
            return "redirect:/post";
        }
        model.addAttribute("post", opt.get());
        return "post/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("post") @Valid Post post, BindingResult bindingResult,
                          HttpSession session, RedirectAttributes attr) {
        Post aGuardar;
        if (post.getIdPost() == 0) {
            aGuardar = new Post();
            aGuardar.setUser(usuarioSesion(session));
        } else {
            Optional<Post> opt = postRepository.findById(post.getIdPost());
            if (opt.isEmpty() || !puedeModificar(opt.get(), session)) {
                attr.addFlashAttribute("error", "No tienes permiso para editar este post");
                return "redirect:/post";
            }
            aGuardar = opt.get();
        }

        if (bindingResult.hasErrors()) {
            return "post/form";
        }

        aGuardar.setTitle(post.getTitle().trim());
        aGuardar.setSummary(post.getSummary());
        aGuardar.setContent(post.getContent());
        aGuardar.setPublished(post.isPublished());
        postRepository.save(aGuardar);

        attr.addFlashAttribute("msg", post.getIdPost() == 0 ? "Post creado exitosamente" : "Post actualizado exitosamente");
        return "redirect:/post";
    }

    @PostMapping("/borrar")
    public String borrarPost(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Optional<Post> opt = postRepository.findById(id);
        if (opt.isPresent() && puedeModificar(opt.get(), session)) {
            postRepository.deleteById(id);
            attr.addFlashAttribute("msg", "Post borrado exitosamente");
        } else {
            attr.addFlashAttribute("error", "No tienes permiso para borrar este post");
        }
        return "redirect:/post";
    }
}
