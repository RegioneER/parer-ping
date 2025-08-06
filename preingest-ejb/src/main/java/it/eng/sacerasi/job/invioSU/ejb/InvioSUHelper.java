/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.job.invioSU.ejb;

import it.eng.sacerasi.entity.*;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.viewEntity.PigVDettStrumUrb;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.util.List;

/**
 *
 * @author gilioli_p
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "InvioSUHelper")
@LocalBean
public class InvioSUHelper extends GenericHelper {

    public List<Long> getIdStrumentiUrbanisticiDaInviare() {
	Query q = getEntityManager().createQuery(
		"SELECT strumentiUrbanistici.idStrumentiUrbanistici FROM PigStrumentiUrbanistici strumentiUrbanistici "
			+ "WHERE strumentiUrbanistici.tiStato = :tiStato ");
	q.setParameter("tiStato", PigStrumentiUrbanistici.TiStato.RICHIESTA_INVIO);
	return q.getResultList();
    }

    /**
     * @deprecated could not resolve property: nmDocumento
     *
     * @param idStrumentiUrbanistici strumento urbanistico
     *
     * @return Tipo del documento principale
     */
    @Deprecated
    public String getTipoDocumentoPrincipale(long idStrumentiUrbanistici) {
	Query q = getEntityManager().createQuery(
		"SELECT strumUrbValDoc.nmDocumento FROM PigStrumUrbDocumenti strumUrbDocumenti "
			+ "JOIN strumUrbDocumenti.pigStrumUrbValDoc strumUrbValDoc "
			+ "JOIN strumUrbDocumenti.pigStrumentiUrbanistici strumentiUrbanistici "
			+ "WHERE strumentiUrbanistici.idStrumentiUrbanistici = :idStrumentiUrbanistici "
			+ "AND strumUrbValDoc.flDocPrincipale = '1' ");

	List<String> lista = q.getResultList();
	if (lista.size() == 1) {
	    return lista.get(0);
	} else {
	    return null;
	}
    }

    public List<Object[]> getDocumenti(long idStrumentiUrbanistici) {
	Query q = getEntityManager().createQuery(
		"SELECT strumUrbValDoc.nmTipoDocumento, strumUrbDocumenti.nmFileOrig, strumUrbValDoc.flDocPrincipale "
			+ "FROM PigStrumUrbDocumenti strumUrbDocumenti "
			+ "JOIN strumUrbDocumenti.pigStrumUrbValDoc strumUrbValDoc "
			+ "JOIN strumUrbDocumenti.pigStrumentiUrbanistici strumentiUrbanistici "
			+ "WHERE strumentiUrbanistici.idStrumentiUrbanistici = :idStrumentiUrbanistici "
			+ "AND strumUrbDocumenti.flDeleted = '0' "
			+ "ORDER BY strumUrbValDoc.idStrumUrbValDoc ASC ");
	q.setParameter("idStrumentiUrbanistici", idStrumentiUrbanistici);
	return q.getResultList();
    }

    public List<PigStrumUrbCollegamenti> getCollegamenti(long idStrumentiUrbanistici) {
	Query q = getEntityManager().createQuery(
		"SELECT strumUrbCollegamenti FROM PigStrumUrbCollegamenti strumUrbCollegamenti "
			+ "JOIN strumUrbCollegamenti.pigStrumentiUrbanistici strumentiUrbanistici "
			+ "WHERE strumentiUrbanistici.idStrumentiUrbanistici = :idStrumentiUrbanistici ");
	q.setParameter("idStrumentiUrbanistici", idStrumentiUrbanistici);
	return q.getResultList();
    }

    /**
     * @deprecated PigVDettStrumentoUrbanistico is not mapped
     *
     * @param idStrumentiUrbanistici strumento urbanistico
     *
     * @return lista di {@link PigVDettStrumUrb}
     */
    @Deprecated
    public List<PigVDettStrumUrb> getDettaglioStrumentoUrbanistico(long idStrumentiUrbanistici) {
	Query q = getEntityManager().createQuery(
		"SELECT dettStrumentoUrbanistico FROM PigVDettStrumentoUrbanistico dettStrumentoUrbanistico "
			+ "WHERE dettStrumentoUrbanistico.idStrumentiUrbanistici = :idStrumentiUrbanistici "
			+ "AND dettStrumentoUrbanistico.flEsitoVerifica = '0' "
			+ "AND dettStrumentoUrbanistico.flDeleted = '0' ");
	q.setParameter("idStrumentiUrbanistici", idStrumentiUrbanistici);
	return q.getResultList();
    }

    public List<PigStrumUrbDocumenti> getDocumentiDaInviare(long idStrumentiUrbanistici) {
	Query q = getEntityManager().createQuery("SELECT strumUrbDocumenti "
		+ "FROM PigStrumUrbDocumenti strumUrbDocumenti "
		+ "JOIN strumUrbDocumenti.pigStrumUrbValDoc strumUrbValDoc "
		+ "JOIN strumUrbDocumenti.pigStrumentiUrbanistici strumentiUrbanistici "
		+ "WHERE strumentiUrbanistici.idStrumentiUrbanistici = :idStrumentiUrbanistici "
		+ "AND strumUrbDocumenti.flDeleted = '0' "
		+ "ORDER BY strumUrbValDoc.idStrumUrbValDoc ASC ");
	q.setParameter("idStrumentiUrbanistici", idStrumentiUrbanistici);
	return q.getResultList();
    }

    public boolean existsPigObjectPerVersatore(long idVers, String cdKeyObject) {
	Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
		+ "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject ");
	q.setParameter("idVers", idVers);
	q.setParameter("cdKeyObject", cdKeyObject);
	return !q.getResultList().isEmpty();
    }

    public boolean existsPigObjectPerVersatoreStrumUrbInAttesaFile(long idVers,
	    String cdKeyObject) {
	Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
		+ "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject "
		+ "AND obj.pigTipoObject.nmTipoObject = 'StrumentoUrbanistico' "
		+ "AND obj.tiStatoObject = 'IN_ATTESA_FILE' ");
	q.setParameter("idVers", idVers);
	q.setParameter("cdKeyObject", cdKeyObject);
	return !q.getResultList().isEmpty();
    }

    public boolean existsPigObjectPerVersatoreStrumUrbAnnullato(long idVers, String cdKeyObject) {
	Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
		+ "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject "
		+ "AND obj.pigTipoObject.nmTipoObject = 'StrumentoUrbanistico' "
		+ "AND obj.tiStatoObject = 'ANNULLATO' ");
	q.setParameter("idVers", idVers);
	q.setParameter("cdKeyObject", cdKeyObject);
	return !q.getResultList().isEmpty();
    }

    public boolean existsPigObjectPerVersatoreNoStrumUrb(long idVers, String cdKeyObject) {
	Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
		+ "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject "
		+ "AND obj.pigTipoObject.nmTipoObject != 'StrumentoUrbanistico' ");
	q.setParameter("idVers", idVers);
	q.setParameter("cdKeyObject", cdKeyObject);
	return !q.getResultList().isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigObject getPigObjectPerVersatoreStrumUrbInNewTx(long idVers, String cdKeyObject) {
	return getPigObjectPerVersatoreStrumUrb(idVers, cdKeyObject);
    }

    public PigObject getPigObjectPerVersatoreStrumUrb(long idVers, String cdKeyObject) {
	Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
		+ "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject "
		+ "AND obj.pigTipoObject.nmTipoObject = 'StrumentoUrbanistico' ");
	q.setParameter("idVers", idVers);
	q.setParameter("cdKeyObject", cdKeyObject);
	List<PigObject> listaOggetti = q.getResultList();
	if (!listaOggetti.isEmpty()) {
	    return listaOggetti.get(0);
	}
	return null;
    }

}
