/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version. <p/> This program is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should have received a copy
 * of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

// package it.eng.parer.slite.gen;
//
// import it.eng.parer.slite.gen.tablebean.Cms_MenuRowBean;
// import it.eng.parer.slite.gen.tablebean.Cms_MenuTableBean;
// import it.eng.spagoCore.error.EMFError;
// import it.eng.spagoLite.FrameElement;
// import it.eng.spagoLite.db.oracle.query.Query;
// import it.eng.spagoLite.security.menu.impl.Link;
// import it.eng.spagoLite.security.menu.impl.Menu;
// import java.math.BigDecimal;
//
// import java.sql.Types;
// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
//
// import org.dom4j.Element;
//
/// **
// *
// * @author Enrico Grillini
// *
// */
// public class MenuUtil {
//
// public static final String ROOT = "MENU";
//
//
//
// public static Menu getMenuTableBean() throws EMFError {
//// Query query = new Query();
//// query.setStatement(MENU);
//// query.addParametro(Types.VARCHAR, ROOT);
//
// //return Cms_MenuTableBean.Factory.load(query, -1);
// Menu menuTableBean = new Menu();
// Cms_MenuRowBean rootMenu = new Cms_MenuRowBean();
// rootMenu.setCodmenu("Menu");
// rootMenu.setDescr_breve("Menu");
// rootMenu.setDescr_lunga("Menu");
// rootMenu.setBigDecimal("level", BigDecimal.ONE);
// Cms_MenuRowBean volumeMenu = new Cms_MenuRowBean();
// volumeMenu.setCodmenu("Menu.Volumi");
// volumeMenu.setDescr_breve("Gestione Volumi");
// volumeMenu.setDescr_lunga("Gestione Volumi");
// volumeMenu.setLink("Volumi.html?operation=initOnClick&cleanhistory=true");
// volumeMenu.setCodmenu_padre("Menu");
// volumeMenu.setBigDecimal("level", new BigDecimal(2));
// Cms_MenuRowBean compMenu = new Cms_MenuRowBean();
// compMenu.setCodmenu("Menu.Componenti");
// compMenu.setDescr_breve("Gestione Componenti");
// compMenu.setDescr_lunga("Gestione Componenti");
// compMenu.setLink("Componenti.html?operation=initOnClick&cleanhistory=true");
// compMenu.setCodmenu_padre("Menu");
// compMenu.setBigDecimal("level", new BigDecimal(2));
// Cms_MenuRowBean listaVolumiErrMenu = new Cms_MenuRowBean();
// listaVolumiErrMenu.setCodmenu("Menu.VolumiErrore");
// listaVolumiErrMenu.setDescr_breve("Lista volumi in errore");
// listaVolumiErrMenu.setDescr_lunga("Lista volumi in errore");
// listaVolumiErrMenu.setLink("Volumi.html?operation=loadListaVolumiErrore&cleanhistory=true");
// listaVolumiErrMenu.setCodmenu_padre("Menu");
// listaVolumiErrMenu.setBigDecimal("level", new BigDecimal(2));
// Cms_MenuRowBean listaVolumiFirmMenu = new Cms_MenuRowBean();
// listaVolumiFirmMenu.setCodmenu("Menu.VolumiFirma");
// listaVolumiFirmMenu.setDescr_breve("Lista volumi da firmare");
// listaVolumiFirmMenu.setDescr_lunga("Lista volumi da firmare");
// listaVolumiFirmMenu.setLink("Volumi.html?operation=loadListaVolumiFirma&cleanhistory=true");
// listaVolumiFirmMenu.setCodmenu_padre("Menu");
// listaVolumiFirmMenu.setBigDecimal("level", new BigDecimal(2));
// Cms_MenuRowBean unitaDocumentarieMenu = new Cms_MenuRowBean();
// unitaDocumentarieMenu.setCodmenu("Menu.UnitaDocumentarie");
// unitaDocumentarieMenu.setDescr_breve("Gestione Unità Documentarie");
// unitaDocumentarieMenu.setDescr_lunga("Gestione Unità Documentarie");
// unitaDocumentarieMenu.setLink("UnitaDocumentarie.html?operation=initOnClick&cleanhistory=true");
// unitaDocumentarieMenu.setCodmenu_padre("Menu");
// unitaDocumentarieMenu.setBigDecimal("level", new BigDecimal(2));
// Cms_MenuRowBean listaCriteriRaggrMenu = new Cms_MenuRowBean();
// listaCriteriRaggrMenu.setCodmenu("Menu.ListaCriteriRaggr");
// listaCriteriRaggrMenu.setDescr_breve("Lista criteri di raggruppamento");
// listaCriteriRaggrMenu.setDescr_lunga("Lista criteri di raggruppamento");
// listaCriteriRaggrMenu.setLink("UnitaDocumentarie.html?operation=loadListaCriteriRaggr&cleanhistory=true");
// listaCriteriRaggrMenu.setCodmenu_padre("Menu");
// listaCriteriRaggrMenu.setBigDecimal("level", new BigDecimal(2));
// Cms_MenuRowBean criteriRaggrMenu = new Cms_MenuRowBean();
// criteriRaggrMenu.setCodmenu("Menu.CriteriRaggr");
// criteriRaggrMenu.setDescr_breve("Crea nuovo criterio di raggruppamento");
// criteriRaggrMenu.setDescr_lunga("Crea nuovo criterio di raggruppamento");
// criteriRaggrMenu.setLink("UnitaDocumentarie.html?operation=creaCriterioRaggr&ricerca=false&cleanhistory=true");
// criteriRaggrMenu.setCodmenu_padre("Menu");
// criteriRaggrMenu.setBigDecimal("level", new BigDecimal(2));
//
// menuTableBean.add(rootMenu);
// menuTableBean.add(volumeMenu);
// menuTableBean.add(compMenu);
// menuTableBean.add(listaVolumiErrMenu);
// menuTableBean.add(listaVolumiFirmMenu);
// menuTableBean.add(unitaDocumentarieMenu);
// menuTableBean.add(listaCriteriRaggrMenu);
// menuTableBean.add(criteriRaggrMenu);
// return menuTableBean;
// }
//
// public static void populateMenu() throws EMFError {
// Cms_MenuTableBean menuTableBean = getMenuTableBean();
// List<Node> list = new ArrayList<Node>();
// for (Cms_MenuRowBean cms_MenuRowBean : menuTableBean) {
// int level = cms_MenuRowBean.getBigDecimal("level").intValue();
// Node node = new Node(cms_MenuRowBean);
//
// if (list.size() < level) {
// list.add(null);
// }
//
// if (level > 1) {
// list.get(level - 2).add(node);
// }
//
// list.set(level - 1, node);
// }
//
// for (Node childNode : list.get(0)) {
// populateMenu(menu, childNode);
// }
// }
//
// private static void populateMenu(Menu menu, Node node) {
//
// if (!node.hasChild()) {
// Link childLink = new Link(node.getCms_MenuRowBean().getCodmenu(), node.getCms_MenuRowBean().getDescr_breve(),
// node.getCms_MenuRowBean().getDescr_lunga(), node.getCms_MenuRowBean().getLink());
// menu.add(childLink);
// } else {
// Menu childMenu = new Menu(node.getCms_MenuRowBean().getCodmenu(), node.getCms_MenuRowBean().getDescr_breve(),
// node.getCms_MenuRowBean().getDescr_lunga());
// menu.add(childMenu);
//
// for (Node childNode : node) {
// populateMenu(childMenu, childNode);
// }
// }
// }
//
// /**
// *
// * Classe di utilita' per agevolare il popolamento del Menu
// *
// * @author Enrico Grillini
// *
// */
// private static class Node extends FrameElement implements Iterable<Node> {
//
// private List<Node> child;
// private Cms_MenuRowBean cms_MenuRowBean;
//
// public Node(Cms_MenuRowBean cms_MenuRowBean) {
// this.child = new ArrayList<Node>();
// this.cms_MenuRowBean = cms_MenuRowBean;
// }
//
// public Cms_MenuRowBean getCms_MenuRowBean() {
// return cms_MenuRowBean;
// }
//
// public boolean hasChild() {
// return child.size() > 0;
// }
//
// public void add(Node node) {
// child.add(node);
// }
//
// public Iterator<Node> iterator() {
// return child.iterator();
// }
//
// @Override
// public Element asXml() {
// Element element = super.asXml();
// element.addAttribute("codice", cms_MenuRowBean.getCodmenu());
//
// for (Node child : this) {
// element.add(child.asXml());
// }
//
// return element;
// }
//
// }
//
// }
