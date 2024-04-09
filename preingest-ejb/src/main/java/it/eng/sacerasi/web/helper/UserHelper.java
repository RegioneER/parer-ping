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

/// *
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
// package it.eng.sacerasi.web.helper;
//
//// import it.eng.sacerasi.entity.PigUsrUser;
// import java.io.Serializable;
// import java.util.Calendar;
// import java.util.Date;
// import javax.ejb.LocalBean;
// import javax.ejb.Stateless;
// import javax.persistence.EntityManager;
// import javax.persistence.NoResultException;
// import javax.persistence.PersistenceContext;
// import javax.persistence.Query;
// import org.apache.log4j.Logger;
//
/// **
// *
// * @author Gilioli_P
// */
// @Stateless
// @LocalBean
// public class UserHelper implements Serializable {
//
// Logger log = Logger.getLogger(UserHelper.class);
// @PersistenceContext(unitName = "SacerAsiJPA")
// private EntityManager em;

// DA: eliminato per modifica iam
// public PigUsrUser findUser(String username, String password) throws Exception {
// Query q = em.createQuery("SELECT u FROM PigUsrUser u WHERE (u.nmUserId = :username AND u.cdPsw = :passwd)");
// q.setParameter("username", username);
// q.setParameter("passwd", password);
// PigUsrUser user = null;
// try {
// user = (PigUsrUser) q.getSingleResult();
// } catch (NoResultException e) {
// throw new Exception();
// }
// if (user != null) {
// return user;
// } else {
// throw new Exception();
// }
// }

// DA: eliminato per modifica iam
// public void updateUserPwd(long idUtente, String oldpassword, String password, Date scadenzaPwd) throws
// NoResultException, Exception {
// PigUsrUser user = findUserById(idUtente);
// if (!oldpassword.equals(user.getCdPsw())) {
// throw new Exception();
// }
// user.setDtScadPsw(scadenzaPwd);
// user.setCdPsw(password);
// }

// DA: eliminato per modifica iam
// public PigUsrUser findUserById(long idUtente) throws NoResultException {
// Query q = em.createQuery("SELECT u FROM PigUsrUser u WHERE u.idUser = :iduser");
// q.setParameter("iduser", idUtente);
// PigUsrUser user = null;
// user = (PigUsrUser) q.getSingleResult();
// return user;
// }

// DA: eliminato per modifica iam
// public void resetPwd(long idUtente, String randomPwd, Date scad) {
// PigUsrUser user = findUserById(idUtente);
// user.setDtScadPsw(scad);
// user.setCdPsw(randomPwd);
// }

// DA: eliminato per modifica iam
// public void resetPwd(long idUtente, String randomPwd) {
// resetPwd(idUtente, randomPwd, Calendar.getInstance().getTime());
// }

// DA: eliminato per modifica iam
// public void deleteUsrUser(PigUsrUser user) {
// if (user != null) {
// em.remove(user);
// em.flush();
// }
// }
// }
