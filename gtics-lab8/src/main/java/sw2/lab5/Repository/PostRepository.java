package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sw2.lab5.Entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findAllByOrderByIdPostDesc();

    List<Post> findByPublishedTrueOrderByIdPostDesc();

    @Query("select p from Post p where p.published = true or p.user.idUser = :idUser order by p.idPost desc")
    List<Post> listarParaUsuario(@Param("idUser") int idUser);

    long countByUser_IdUser(int idUser);
}
