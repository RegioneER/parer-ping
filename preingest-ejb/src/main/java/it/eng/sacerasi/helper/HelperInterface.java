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

package it.eng.sacerasi.helper;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 *
 * @author Bonora_L
 */
public interface HelperInterface {

    public void setEntityManager(EntityManager entityManager);

    public EntityManager getEntityManager();

    public <T> void insertEntity(T entity, boolean forceFlush);

    public <T> void removeEntity(T entity, boolean forceFlush);

    public <T> T findById(Class<T> entityClass, Serializable id);

    public <T> T findByIdWithLock(Class<T> entityClass, Serializable id);

    public <T> T findViewById(Class<T> viewEntityClass, Serializable id);
}
