package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sw2.lab5.Entity.SolicitudAmistad;

import java.util.List;

@Repository
public interface SolicitudAmistadRepository extends JpaRepository<SolicitudAmistad, Integer> {

    List<SolicitudAmistad> findByReceptor_IdUserAndEstado(int idUser, String estado);

    List<SolicitudAmistad> findByEmisor_IdUserAndEstado(int idUser, String estado);

    @Query("select s from SolicitudAmistad s where s.estado = 'aceptada' and (s.emisor.idUser = :idUser or s.receptor.idUser = :idUser)")
    List<SolicitudAmistad> listarAmistades(@Param("idUser") int idUser);

    @Query("select s from SolicitudAmistad s where s.emisor.idUser = :idUser or s.receptor.idUser = :idUser")
    List<SolicitudAmistad> listarRelaciones(@Param("idUser") int idUser);

    @Query("select count(s) from SolicitudAmistad s where (s.emisor.idUser = :a and s.receptor.idUser = :b) or (s.emisor.idUser = :b and s.receptor.idUser = :a)")
    long contarEntre(@Param("a") int a, @Param("b") int b);
}
