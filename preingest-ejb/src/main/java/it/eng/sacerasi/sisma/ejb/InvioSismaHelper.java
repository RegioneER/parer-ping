/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.sacerasi.sisma.ejb;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSisma;
//import it.eng.sacerasi.entity.PigSismaCollegamenti;
import it.eng.sacerasi.entity.PigSismaDocumenti;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.viewEntity.PigVDettSisma;

/**
 *
 * @author gilioli_p
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "InvioSismaHelper")
@LocalBean
public class InvioSismaHelper extends GenericHelper {

    public static final String NOME_TIPO_OGGETTO_DA_TRASFORMARE = "ProgettoRicostruzione";
    // public static final String NOME_TIPO_REGISTRO = "PROGETTI RICOSTRUZIONE SISMA";
    public static final String NOME_FILE_XML = "ProgettiSisma.xml";

    public List<Long> getIdSismaDaInviare() {
        Query q = getEntityManager().createQuery("SELECT s.idSisma FROM PigSisma s WHERE s.tiStato = :tiStato ");
        q.setParameter("tiStato", PigSisma.TiStato.RICHIESTA_INVIO);
        List<Long> lista = q.getResultList();
        return lista;
    }

    /*
     * public String getTipoDocumentoPrincipale(long idSisma) { Query q = getEntityManager()
     * .createQuery("SELECT sismaValDoc.nmDocumento FROM PigSismaDocumenti sismaDocumenti " +
     * "JOIN sismaDocumenti.pigSismaValDoc sismaValDoc " + "JOIN sismaDocumenti.pigSisma sisma " +
     * "WHERE sisma.idSisma = :idSisma " + "AND sismaValDoc.flDocPrincipale = '1' ");
     *
     * List<String> lista = (List<String>) q.getResultList(); if (lista.size() == 1) { return lista.get(0); } else {
     * return null; } }
     */
    public List<Object[]> getDocumenti(long idSisma) {
        Query q = getEntityManager().createQuery(
                "SELECT sismaValDoc.nmTipoDocumento, sismaDocumenti.nmFileOrig, sismaValDoc.flDocPrincipale "
                        + "FROM PigSismaDocumenti sismaDocumenti JOIN sismaDocumenti.pigSismaValDoc sismaValDoc "
                        + "JOIN sismaDocumenti.pigSisma sisma WHERE sisma.idSisma = :idSisma "
                        + "AND sismaDocumenti.flDeleted = '0' ORDER BY sismaValDoc.idSismaValDoc ASC ");
        q.setParameter("idSisma", idSisma);
        List<Object[]> lista = (List<Object[]>) q.getResultList();
        return lista;
    }

    public List<PigVDettSisma> getDettaglioSisma(long idSisma) {
        Query q = getEntityManager().createQuery("SELECT d FROM PigVDettSisma d WHERE d.idSisma = :idSisma "
                + "AND d.flEsitoVerifica = '0' AND d.flDeleted = '0' ");
        q.setParameter("idSisma", HibernateUtils.bigDecimalFrom(idSisma));
        List<PigVDettSisma> lista = (List<PigVDettSisma>) q.getResultList();
        return lista;
    }

    public List<PigSismaDocumenti> getDocumentiDaInviare(long idSisma) {
        Query q = getEntityManager().createQuery("SELECT sismaDocumenti FROM PigSismaDocumenti sismaDocumenti "
                + "JOIN sismaDocumenti.pigSismaValDoc sismaValDoc JOIN sismaDocumenti.pigSisma s "
                + "WHERE s.idSisma = :idSisma AND sismaDocumenti.flDeleted = '0' "
                + "ORDER BY sismaValDoc.idSismaValDoc ASC ");
        q.setParameter("idSisma", idSisma);
        List<PigSismaDocumenti> lista = (List<PigSismaDocumenti>) q.getResultList();
        return lista;
    }

    public boolean existsPigObjectPerVersatore(long idVers, String cdKeyObject) {
        Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
                + "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject ");
        q.setParameter("idVers", idVers);
        q.setParameter("cdKeyObject", cdKeyObject);
        return !q.getResultList().isEmpty();
    }

    public boolean existsPigObjectPerVersatoreSismaInAttesaFile(long idVers, String cdKeyObject) {
        Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
                + "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject "
                + "AND obj.pigTipoObject.nmTipoObject = :nmTipoObject " + "AND obj.tiStatoObject = 'IN_ATTESA_FILE' ");
        q.setParameter("idVers", idVers);
        q.setParameter("cdKeyObject", cdKeyObject);
        q.setParameter("nmTipoObject", NOME_TIPO_OGGETTO_DA_TRASFORMARE);
        return !q.getResultList().isEmpty();
    }

    public boolean existsPigObjectPerVersatoreSismaAnnullato(long idVers, String cdKeyObject) {
        Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
                + "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject "
                + "AND obj.pigTipoObject.nmTipoObject = :nmTipoObject " + "AND obj.tiStatoObject = 'ANNULLATO' ");
        q.setParameter("idVers", idVers);
        q.setParameter("cdKeyObject", cdKeyObject);
        q.setParameter("nmTipoObject", NOME_TIPO_OGGETTO_DA_TRASFORMARE);
        return !q.getResultList().isEmpty();
    }

    public boolean existsPigObjectPerVersatoreNoSisma(long idVers, String cdKeyObject) {
        Query q = getEntityManager().createQuery("SELECT obj FROM PigObject obj WHERE obj.pigVer.idVers = :idVers "
                + "AND obj.cdKeyObject = :cdKeyObject AND obj.pigTipoObject.nmTipoObject != :nmTipoObject ");
        q.setParameter("idVers", idVers);
        q.setParameter("cdKeyObject", cdKeyObject);
        q.setParameter("nmTipoObject", NOME_TIPO_OGGETTO_DA_TRASFORMARE);
        return !q.getResultList().isEmpty();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigObject getPigObjectPerVersatoreSismaInNewTx(long idVers, String cdKeyObject) {
        return getPigObjectPerVersatoreSisma(idVers, cdKeyObject);
    }

    public PigObject getPigObjectPerVersatoreSisma(long idVers, String cdKeyObject) {
        Query q = getEntityManager().createQuery("SELECT obj " + "FROM PigObject obj "
                + "WHERE obj.pigVer.idVers = :idVers " + "AND obj.cdKeyObject = :cdKeyObject "
                + "AND obj.pigTipoObject.nmTipoObject = :nmTipoObject ");
        q.setParameter("idVers", idVers);
        q.setParameter("cdKeyObject", cdKeyObject);
        q.setParameter("nmTipoObject", NOME_TIPO_OGGETTO_DA_TRASFORMARE);
        List<PigObject> listaOggetti = q.getResultList();
        if (!listaOggetti.isEmpty()) {
            return listaOggetti.get(0);
        }
        return null;
    }

}
