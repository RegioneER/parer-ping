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

package it.eng.sacerasi.job.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NfsUtils {
    private static final Logger log = LoggerFactory.getLogger(NfsUtils.class);

    private NfsUtils() {

    }

    public static void createEmptyDir(String fullPath) throws IOException {
        Path dirPath = Paths.get(fullPath);
        File directory = dirPath.toFile();
        if (directory.exists()) {
            log.debug("La cartella {} esiste, la dobbiamo svuotare", fullPath);
            File[] files = directory.listFiles((dir, name) -> {
                boolean toDelete = !name.matches("\\.nfs.+");
                log.debug("File {} lo devo cancellare? {}", name, toDelete);
                return toDelete;
            });
            if (files != null) {
                for (File file : files) {
                    log.debug("Procedo alla cancellazione di {}", file.getAbsolutePath());
                    FileUtils.forceDelete(file);
                }
            }
        } else {
            log.debug("La cartella {} non esiste, la creo", fullPath);
            Files.createDirectory(dirPath);
        }
    }
}
