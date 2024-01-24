package Storage;

import com.example.outfitmakerfake.Entity.Utente;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginService {

    private UtenteDAO utenteDAO;

    public LoginService() {
    }

    public LoginService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }


    public Task<Boolean> effettuaLogin(String email, String password){
        return utenteDAO.effettuaLogin(email, password);
    }
}
