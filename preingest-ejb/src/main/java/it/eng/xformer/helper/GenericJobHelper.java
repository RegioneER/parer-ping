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

package it.eng.xformer.helper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.io.IOUtils;

import it.eng.sacerasi.entity.PigObject;
import it.eng.sacerasi.entity.PigSessioneIngest;
import it.eng.sacerasi.entity.PigStatoSessioneIngest;
import it.eng.sacerasi.entity.XfoTrasf;
import it.eng.xformer.common.Constants;

/**
 * @author Cappelli_F
 */
@Stateless
@LocalBean
public class GenericJobHelper extends GenericJobQueryHelper {

    @EJB(mappedName = "java:app/SacerAsync-ejb/TrasformazioniHelper")
    private TrasformazioniHelper trasformazioniHelper;

    public GenericJobHelper() {
        /**
         * SONAR
         */
    }

    public PigSessioneIngest searchCurrentSession(PigObject po) {
        for (PigSessioneIngest psi : po.getPigSessioneIngests()) {
            if (po.getIdLastSessioneIngest().longValue() == psi.getIdSessioneIngest()) {
                return psi;
            }
        }
        return null;
    }

    /*
     * calcolo l'hash in streaming, lento ma mi tutela da eventuali out of memory
     */
    public String calculateHash(InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(Constants.TipoHash.SHA_1);
        DigestInputStream dis = null;
        int bufferSize = 100 * 1024 * 1024; // 100 MB

        try {
            dis = new DigestInputStream(is, md);
            byte[] buffer = new byte[bufferSize];
            while (dis.read(buffer) != -1) {
            }
        } finally {
            IOUtils.closeQuietly(dis);
        }

        byte[] pwdHash = md.digest();
        return toHexBinary(pwdHash);
    }

    public String toHexBinary(byte[] dati) {
        if (dati != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : dati) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changePigObjectAndSessionStateAtomic(long pigId, String stato) {
        PigObject po = trasformazioniHelper.findById(PigObject.class, pigId);
        this.changePigObjectAndSessionState(po, stato, null, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PigObject setPigObjectAndSessionChoosenTransformationAtomic(long pigId, XfoTrasf transformation) {
        PigObject po = trasformazioniHelper.findById(PigObject.class, pigId);

        // salva nome e versione della trasformazione nell'oggetto e nella sua sessione.
        po.setCdTrasf(transformation.getCdTrasf());
        po.setCdVersioneTrasf(transformation.getCdVersioneCor());

        PigSessioneIngest currentSession = this.searchCurrentSession(po);
        currentSession.setCdTrasf(transformation.getCdTrasf());
        currentSession.setCdVersioneTrasf(transformation.getCdVersioneCor());

        return po;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changePigObjectAndSessionStateAtomic(long pigId, String stato, String codiceErrore,
            String descrizioneErrore) {
        PigObject po = trasformazioniHelper.findById(PigObject.class, pigId);
        this.changePigObjectAndSessionState(po, stato, codiceErrore, descrizioneErrore);
    }

    public void changePigObjectAndSessionState(PigObject po, String stato) {
        this.changePigObjectAndSessionState(po, stato, null, null);
    }

    public void changePigObjectAndSessionState(PigObject po, String stato, String codiceErrore,
            String descrizioneErrore) {
        PigSessioneIngest currentSession = searchCurrentSession(po);

        po.setTiStatoObject(stato);
        currentSession.setTiStato(stato);

        // Il campo id_stato_sessione_ingest_corrente andrà riempito con l’identificatore di una nuova riga creata
        // appositamente nella tabella “PIG_STATO_SESSIONE_INGEST”.
        Date now = new Date();

        PigStatoSessioneIngest updatedStatoSessione = new PigStatoSessioneIngest();
        updatedStatoSessione.setPigSessioneIngest(currentSession);
        updatedStatoSessione.setIdVers(currentSession.getPigVer().getIdVers());
        updatedStatoSessione.setTsRegStato(new Timestamp(now.getTime()));
        updatedStatoSessione.setTiStato(stato);

        entityManager.persist(updatedStatoSessione);

        currentSession.addPigStatoSessioneIngest(updatedStatoSessione);
        currentSession.setIdStatoSessioneIngestCor(new BigDecimal(updatedStatoSessione.getIdStatoSessioneIngest()));

        currentSession.setCdErr(codiceErrore);
        currentSession.setDlErr(descrizioneErrore);
    }
}
