
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Colonia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IColonia extends JpaRepository<Colonia, Integer>{
    List<Colonia> findByMunicipioIdMunicipio(Integer idMunicipio);
}
