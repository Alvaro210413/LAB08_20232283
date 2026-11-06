package sw2.lab5.Entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "post")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_post")
    private int idPost;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Usuario user;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 75, message = "Máximo 75 caracteres")
    @Column(nullable = false)
    private String title;

    @Size(max = 255, message = "Máximo 255 caracteres")
    private String summary;

    @NotBlank(message = "El contenido no puede estar vacío")
    private String content;

    @Column(nullable = false)
    private boolean published;

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
