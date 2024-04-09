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

package it.eng.sacerasi.web.util;

import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/*
 * @author Quaranta_M
 * 
 * La trasformazione funziona purché siano soddisatte le seguenti condizioni:
 *  - nomeRowBean = nome Entity + "RowBean" (NB: Dali tronca una eventuale 's' posta alla fine del nome della entity)
 *  - nomeTableBean = nome Entity + "TableBean"
 *  - i nomi delle variabili degli oggetti referenziati dalle entity (e le PK di tali oggetti) hanno la parte finale del nome uguale alle FK dei rowBean
 *  - le PK Embeddable nelle entity devono avere il nome "id"
 * 
 */

public class Transform {

    private Transform() {
    }

    public static AbstractBaseTable entities2TableBean(List entities)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String entityClassName = entities.get(0).getClass().getName()
                .substring(entities.get(0).getClass().getName().lastIndexOf("."));
        String tbClassName = null;
        if (entities.get(0).getClass().getName().startsWith(Constants.ENTITY_PACKAGE_NAME)) {
            tbClassName = Constants.ROWBEAN_PACKAGE_NAME + entityClassName + "TableBean";
        } else {
            tbClassName = Constants.VIEWROWBEAN_PACKAGE_NAME + entityClassName + "TableBean";
        }
        Class clazz = Class.forName(tbClassName);
        Constructor constructor = clazz.getConstructor();
        AbstractBaseTable tableBean = (AbstractBaseTable) constructor.newInstance();
        for (Object entity : entities) {
            tableBean.add(Transform.entity2RowBean(entity));
        }
        return tableBean;
    }

    public static BaseRowInterface entity2RowBean(Object entity) throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String entityClassName = getClassName(entity);
        String rbClassName = null;
        if (entity.getClass().getName().startsWith(Constants.ENTITY_PACKAGE_NAME)) {
            rbClassName = Constants.ROWBEAN_PACKAGE_NAME + "." + entityClassName + "RowBean";
        } else {
            rbClassName = Constants.VIEWROWBEAN_PACKAGE_NAME + "." + entityClassName + "RowBean";
        }
        Class clazz = Class.forName(rbClassName);
        Constructor constructor = clazz.getConstructor();
        Object rowBean = constructor.newInstance();
        ((JEEBaseRowInterface) rowBean).entityToRowBean(entity);
        return (BaseRowInterface) rowBean;
    }

    public static Object rowBean2Entity(Object rowBean) {
        return ((JEEBaseRowInterface) rowBean).rowBeanToEntity();
    }

    private static String getClassName(Object obj) {
        final List<String> proxySuffixes = Arrays.asList("_$$", "$HibernateProxy");
        String className = obj.getClass().getSimpleName();
        for (String s : proxySuffixes) {
            if (className.contains(s)) {
                className = className.substring(0, className.indexOf(s));
                break;
            }
        }
        return className;
    }

}
