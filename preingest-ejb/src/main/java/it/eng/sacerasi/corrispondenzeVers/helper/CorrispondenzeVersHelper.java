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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.sacerasi.corrispondenzeVers.helper;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

import it.eng.paginator.util.HibernateUtils;
import it.eng.sacerasi.helper.GenericHelper;
import it.eng.sacerasi.viewEntity.PigVLisStrutVersSacer;
import it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class CorrispondenzeVersHelper extends GenericHelper {

    public List<PigVLisStrutVersSacer> getIdOrganizIamStrut(long idTipoObj) {
        final TypedQuery<PigVLisStrutVersSacer> query = getEntityManager().createQuery(
                "SELECT DISTINCT new it.eng.sacerasi.viewEntity.PigVLisStrutVersSacer(pigv.id.idOrganizIamStrut, pigv.nmUseridSacer) "
                        + "FROM PigVLisStrutVersSacer pigv WHERE pigv.idTipoObject = :idTipoObject",
                PigVLisStrutVersSacer.class).setParameter("idTipoObject", HibernateUtils.bigDecimalFrom(idTipoObj));
        return query.getResultList();
    }

    // Metodi replicati anche in ControlliPrepXml
    public List<PigVLisStrutVersSacer> getIdOrganizIamStrut(long idTipoObj, BigDecimal idOrganizIamStrut) {
        final TypedQuery<PigVLisStrutVersSacer> query = getEntityManager().createQuery(
                "SELECT DISTINCT new it.eng.sacerasi.viewEntity.PigVLisStrutVersSacer(pigv.id.idOrganizIamStrut, pigv.nmUseridSacer, pigv.cdPasswordSacer) "
                        + "FROM PigVLisStrutVersSacer pigv WHERE pigv.idTipoObject = :idTipoObject AND pigv.id.idOrganizIamStrut = :idOrganizIamStrut",
                PigVLisStrutVersSacer.class).setParameter("idTipoObject", HibernateUtils.bigDecimalFrom(idTipoObj))
                .setParameter("idOrganizIamStrut", idOrganizIamStrut);
        return query.getResultList();
    }

    // Metodi replicati anche in ControlliPrepXml
    public UsrVAbilStrutSacerXping getStrutturaAbilitata(BigDecimal idOrganizIam, String nmUserId) {
        final TypedQuery<UsrVAbilStrutSacerXping> query = getEntityManager().createQuery(
                "SELECT DISTINCT new it.eng.sacerasi.viewEntity.UsrVAbilStrutSacerXping(u.idAmbiente, u.nmAmbiente, u.idEnte, u.nmEnte, u.id.idStrut, u.nmStrut) "
                        + "FROM UsrVAbilStrutSacerXping u WHERE u.idOrganizIamStrut = :idOrganizIam AND u.nmUserid = :nmUserId",
                UsrVAbilStrutSacerXping.class).setParameter("idOrganizIam", idOrganizIam)
                .setParameter("nmUserId", nmUserId);
        return query.getSingleResult();
    }

    // Metodi replicati anche in ControlliPrepXml
    public BigDecimal getStrutturaAbilitata(String nmAmbiente, String nmEnte, String nmStrut, String nmUserId) {
        BigDecimal idOrganizIam = null;
        List<BigDecimal> organizs = getEntityManager().createQuery("SELECT DISTINCT (u.idOrganizIamStrut) "
                + "FROM UsrVAbilStrutSacerXping u WHERE u.nmAmbiente = :nmAmbiente AND u.nmEnte = :nmEnte AND u.nmStrut = :nmStrut AND u.nmUserid = :nmUserId",
                BigDecimal.class).setParameter("nmAmbiente", nmAmbiente).setParameter("nmEnte", nmEnte)
                .setParameter("nmStrut", nmStrut).setParameter("nmUserId", nmUserId).getResultList();
        if (organizs != null && !organizs.isEmpty()) {
            idOrganizIam = organizs.get(0);
        }
        return idOrganizIam;
    }
}
