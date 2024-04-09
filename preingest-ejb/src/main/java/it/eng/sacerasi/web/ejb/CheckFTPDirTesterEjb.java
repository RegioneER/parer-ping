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

package it.eng.sacerasi.web.ejb;

import it.eng.sacerasi.entity.PigVers;
import it.eng.sacerasi.slite.gen.tablebean.PigVersTableBean;
import it.eng.sacerasi.web.helper.AmministrazioneHelper;
import it.eng.sacerasi.web.util.Transform;
import it.eng.sacerasi.ws.dto.RispostaControlli;
import java.io.File;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.resource.ResourceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;
import org.xadisk.filesystem.exceptions.DirectoryNotEmptyException;
import org.xadisk.filesystem.exceptions.FileAlreadyExistsException;
import org.xadisk.filesystem.exceptions.FileNotExistsException;
import org.xadisk.filesystem.exceptions.FileUnderUseException;
import org.xadisk.filesystem.exceptions.InsufficientPermissionOnFileException;
import org.xadisk.filesystem.exceptions.LockingFailedException;
import org.xadisk.filesystem.exceptions.NoTransactionAssociatedException;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class CheckFTPDirTesterEjb {

    Logger log = LoggerFactory.getLogger(CheckFTPDirTesterEjb.class);
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;

    public PigVersTableBean getPigVersList() {
        PigVersTableBean versatori = new PigVersTableBean();
        List<PigVers> listaPigVers = amministrazioneHelper.getPigVersList(null);
        try {
            if (listaPigVers != null && !listaPigVers.isEmpty()) {
                versatori = (PigVersTableBean) Transform.entities2TableBean(listaPigVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return versatori;
    }

    public RispostaControlli doCheck(String rootFtp, String path) {
        XADiskConnection xadConn = null;
        File checkPath = new File(rootFtp.concat(path));
        String newDir = "checkDir";
        String newFile = "checkFile.txt";
        RispostaControlli risp = new RispostaControlli();
        risp.setrBoolean(false);
        if (StringUtils.isNotBlank(rootFtp) && StringUtils.isNotBlank(path)) {
            try {
                xadConn = xadCf.getConnection();
                // Crea la directory
                File tmpDir = new File(checkPath, newDir);
                File tmpFile = new File(tmpDir, newFile);
                xadConn.createFile(tmpDir, true);
                // Creo il file
                xadConn.createFile(tmpFile, false);
                // Elimino il file
                xadConn.deleteFile(tmpFile);
                // Elimino la directory
                xadConn.deleteFile(tmpDir);
                risp.setrBoolean(true);
            } catch (ResourceException | FileAlreadyExistsException | FileNotExistsException
                    | InsufficientPermissionOnFileException | LockingFailedException | NoTransactionAssociatedException
                    | InterruptedException | DirectoryNotEmptyException | FileUnderUseException e) {
                risp.setDsErr(e.getMessage());
            } finally {
                if (xadConn != null) {
                    xadConn.close();
                }
            }
        }
        return risp;
    }
}
