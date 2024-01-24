package Storage;

import com.google.android.gms.tasks.Task;

public class AreaUtenteService {

    private AreaUtenteDAO areaUtenteDAO;

    public AreaUtenteService() {
    }

    public AreaUtenteService(AreaUtenteDAO areaUtenteDAO) {
        this.areaUtenteDAO = areaUtenteDAO;
    }

    public Task<Boolean> ottieniDatiUtente(String uid, String nome, String cognome, String email, String telefono){
        return areaUtenteDAO.ottieniDatiUtente(uid, nome, cognome, email, telefono);
    }

    public Task<Boolean> modificaDatiUtente(String nome, String cognome, String telefono){
        return areaUtenteDAO.modificaDati(nome, cognome, telefono);
    }
}
