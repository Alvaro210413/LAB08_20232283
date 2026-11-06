package sw2.lab5.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sw2.lab5.Entity.SolicitudAmistad;
import sw2.lab5.Entity.Usuario;
import sw2.lab5.Repository.SolicitudAmistadRepository;
import sw2.lab5.Repository.UsuarioRepository;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/amigos")
public class AmistadController {

    @Autowired
    SolicitudAmistadRepository solicitudRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("")
    public String inicio(Model model, HttpSession session) {
        Usuario yo = (Usuario) session.getAttribute("usuario");

        List<Integer> idsConRelacion = new ArrayList<>();
        for (SolicitudAmistad s : solicitudRepository.listarRelaciones(yo.getIdUser())) {
            idsConRelacion.add(s.getEmisor().getIdUser());
            idsConRelacion.add(s.getReceptor().getIdUser());
        }

        List<Usuario> otrosUsuarios = new ArrayList<>();
        for (Usuario u : usuarioRepository.findAllByOrderByIdUserAsc()) {
            if (u.isActive() && u.getIdUser() != yo.getIdUser() && !idsConRelacion.contains(u.getIdUser())) {
                otrosUsuarios.add(u);
            }
        }

        model.addAttribute("recibidas", solicitudRepository.findByReceptor_IdUserAndEstado(yo.getIdUser(), "pendiente"));
        model.addAttribute("enviadas", solicitudRepository.findByEmisor_IdUserAndEstado(yo.getIdUser(), "pendiente"));
        model.addAttribute("amistades", solicitudRepository.listarAmistades(yo.getIdUser()));
        model.addAttribute("otrosUsuarios", otrosUsuarios);
        return "amigos/lista";
    }

    @PostMapping("/enviar")
    public String enviar(@RequestParam("id") int idReceptor, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Optional<Usuario> opt = usuarioRepository.findById(idReceptor);

        if (opt.isEmpty() || !opt.get().isActive()) {
            attr.addFlashAttribute("error", "El usuario no existe");
        } else if (idReceptor == yo.getIdUser()) {
            attr.addFlashAttribute("error", "No puedes enviarte una solicitud a ti mismo");
        } else if (solicitudRepository.contarEntre(yo.getIdUser(), idReceptor) > 0) {
            attr.addFlashAttribute("error", "Ya existe una solicitud o amistad con ese usuario");
        } else {
            SolicitudAmistad solicitud = new SolicitudAmistad();
            solicitud.setEmisor(usuarioRepository.findById(yo.getIdUser()).get());
            solicitud.setReceptor(opt.get());
            solicitud.setEstado("pendiente");
            solicitud.setFecha(LocalDateTime.now());
            solicitudRepository.save(solicitud);
            attr.addFlashAttribute("msg", "Solicitud enviada a " + opt.get().getFirstname());
        }
        return "redirect:/amigos";
    }

    @PostMapping("/aceptar")
    public String aceptar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Optional<SolicitudAmistad> opt = solicitudRepository.findById(id);

        if (opt.isPresent() && opt.get().getReceptor().getIdUser() == yo.getIdUser()
                && opt.get().getEstado().equals("pendiente")) {
            SolicitudAmistad solicitud = opt.get();
            solicitud.setEstado("aceptada");
            solicitudRepository.save(solicitud);
            attr.addFlashAttribute("msg", "Ahora eres amigo de " + solicitud.getEmisor().getFirstname());
        } else {
            attr.addFlashAttribute("error", "No puedes aceptar esa solicitud");
        }
        return "redirect:/amigos";
    }

    @PostMapping("/rechazar")
    public String rechazar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Optional<SolicitudAmistad> opt = solicitudRepository.findById(id);

        if (opt.isPresent() && opt.get().getReceptor().getIdUser() == yo.getIdUser()
                && opt.get().getEstado().equals("pendiente")) {
            solicitudRepository.delete(opt.get());
            attr.addFlashAttribute("msg", "Solicitud rechazada");
        } else {
            attr.addFlashAttribute("error", "No puedes rechazar esa solicitud");
        }
        return "redirect:/amigos";
    }

    @PostMapping("/cancelar")
    public String cancelar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Optional<SolicitudAmistad> opt = solicitudRepository.findById(id);

        if (opt.isPresent() && opt.get().getEmisor().getIdUser() == yo.getIdUser()
                && opt.get().getEstado().equals("pendiente")) {
            solicitudRepository.delete(opt.get());
            attr.addFlashAttribute("msg", "Solicitud cancelada");
        } else {
            attr.addFlashAttribute("error", "No puedes cancelar esa solicitud");
        }
        return "redirect:/amigos";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("id") int id, HttpSession session, RedirectAttributes attr) {
        Usuario yo = (Usuario) session.getAttribute("usuario");
        Optional<SolicitudAmistad> opt = solicitudRepository.findById(id);

        if (opt.isPresent() && opt.get().getEstado().equals("aceptada")
                && (opt.get().getEmisor().getIdUser() == yo.getIdUser() || opt.get().getReceptor().getIdUser() == yo.getIdUser())) {
            solicitudRepository.delete(opt.get());
            attr.addFlashAttribute("msg", "Amistad eliminada");
        } else {
            attr.addFlashAttribute("error", "No puedes eliminar esa amistad");
        }
        return "redirect:/amigos";
    }
}
