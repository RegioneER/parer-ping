package it.eng.sacerasi.spring;

import it.eng.spagoLite.spring.CustomSaml2AuthenticationSuccessHandler;
import it.eng.sacerasi.grantEntity.UsrUser;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marco Iacolucci
 */
@Component
public class SacerpingSaml2AuthenticationSuccessHandler extends CustomSaml2AuthenticationSuccessHandler {

    @EJB(mappedName = "java:app/SacerAsync-ejb/AmministrazioneHelper")
    private AmministrazioneHelper amministrazioneHelper;

    @Override
    protected List<UtenteDb> findUtentiPerCodiceFiscale(String codiceFiscale) {
        ArrayList<UtenteDb> al = new ArrayList<>();
        List<UsrUser> l = amministrazioneHelper.findByCodiceFiscale(codiceFiscale);
        for (UsrUser usrUser : l) {
            UtenteDb u = new UtenteDb();
            u.setId(usrUser.getIdUserIam());
            u.setCodiceFiscale(usrUser.getCdFisc());
            u.setDataScadenzaPassword(u.getDataScadenzaPassword());
            u.setUsername(usrUser.getNmUserid());
            al.add(u);
        }
        return al;
    }

    @Override
    protected UtenteDb getUtentePerUsername(String username) {
        UsrUser usrUser = amministrazioneHelper.findUser(username);
        UtenteDb u = new UtenteDb();
        u.setId(usrUser.getIdUserIam());
        u.setCodiceFiscale(usrUser.getCdFisc());
        u.setDataScadenzaPassword(u.getDataScadenzaPassword());
        u.setUsername(usrUser.getNmUserid());
        return u;
    }

    @Override
    protected List<UtenteDb> findUtentiPerUsernameCaseInsensitive(String username) {
        List<UtenteDb> al = new ArrayList<>();
        List<UsrUser> l = amministrazioneHelper.findUtentiPerUsernameCaseInsensitive(username);
        for (UsrUser usrUser : l) {
            UtenteDb u = new UtenteDb();
            u.setId(usrUser.getIdUserIam());
            u.setCodiceFiscale(usrUser.getCdFisc());
            u.setDataScadenzaPassword(u.getDataScadenzaPassword());
            u.setUsername(usrUser.getNmUserid());
            al.add(u);
        }
        return al;
    }

}
