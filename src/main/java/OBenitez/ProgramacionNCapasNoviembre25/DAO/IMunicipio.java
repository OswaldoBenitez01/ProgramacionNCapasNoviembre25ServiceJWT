
package OBenitez.ProgramacionNCapasNoviembre25.DAO;

import OBenitez.ProgramacionNCapasNoviembre25.JPA.Municipio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMunicipio extends JpaRepository<Municipio, Integer>{
    List<Municipio> findByEstadoIdEstado(Integer idEstado);
}
