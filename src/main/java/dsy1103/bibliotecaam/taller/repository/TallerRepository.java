package dsy1103.bibliotecaam.taller.repository;

import dsy1103.bibliotecaam.taller.model.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TallerRepository extends JpaRepository<Taller,Long> {

    @Query("SELECT t FROM Taller t WHERE UPPER(t.nombreTaller) LIKE UPPER(:nombre)")
    List<Taller> findByNombreTaller(@Param("nombre") String nombre);
}
