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

package it.eng.sacerasi.spring;

import it.eng.paginator.ejb.PaginatorImpl;
import it.eng.parer.sacerlog.web.spring.SpagoliteWebMvcConfiguration;
import it.eng.sacerasi.web.action.AmministrazioneAction;
import it.eng.sacerasi.web.action.EntiConvenzionatiAction;
import it.eng.sacerasi.web.action.GestioneJobAction;
import it.eng.sacerasi.web.action.HomeAction;
import it.eng.sacerasi.web.action.MonitoraggioAction;
import it.eng.sacerasi.web.action.SceltaOrganizzazioneAction;
import it.eng.sacerasi.web.action.SismaAction;
import it.eng.sacerasi.web.action.StrumentiUrbanisticiAction;
import it.eng.sacerasi.web.action.TrasformazioniAction;
import it.eng.sacerasi.web.action.VersamentoOggettoAction;
import it.eng.sacerasi.web.security.PreingestAuthenticator;
import it.eng.sacerasi.web.util.ApplicationBasePropertiesSeviceImpl;
import it.eng.spagoLite.actions.RedirectAction;
import it.eng.spagoLite.actions.security.LoginAction;
import it.eng.spagoLite.actions.security.LogoutAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 *
 * @author Marco Iacolucci
 */
@EnableWebMvc
@ComponentScan(basePackages = {
	"it.eng.sacerasi.web", "it.eng.sacerasi.web.rest.controller", "it.eng.sacerasi.ws",
	"it.eng.sacerasi.spring", "it.eng.sacerasi.web.action", "it.eng.sacerasi.slite.gen.action",
	"it.eng.spagoCore", "it.eng.spagoLite" })
@Configuration
public class WebMvcConfiguration extends SpagoliteWebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
	registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	/*
	 * qui si dichiarano le risorse statiche
	 */
	registry.addResourceHandler("/css/**", "/images/**", "/img/**", "/js/**", "/webjars/**")
		.addResourceLocations("/css/", "/images/", "/img/", "/js/", "/webjars/")
		.setCachePeriod(0);
	registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public InternalResourceViewResolver resolver() {
	InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	resolver.setViewClass(JstlView.class);
	resolver.setPrefix("/jsp/");
	resolver.setSuffix(".jsp");
	resolver.setExposedContextBeanNames("ricercheLoader");
	return resolver;
    }

    @Bean(name = "paginator")
    PaginatorImpl paginatorImpl() {
	return new PaginatorImpl();
    }

    /*
     * Classe che va a caricare le autorizzazioni da IAM
     */
    @Bean(name = "authenticator")
    PreingestAuthenticator preingestAuthenticator() {
	return new PreingestAuthenticator();
    }

    @Bean
    RestTemplate restTemplate() {
	SimpleClientHttpRequestFactory c = new SimpleClientHttpRequestFactory();
	c.setReadTimeout(10000);
	c.setConnectTimeout(10000);
	return new RestTemplate(c);
    }

    /*
     * Template da inserire nelle applicazioni che usano SpagoLite e che utilizzano l' Help On line.
     * Deve implementare l'interfaccia IApplicationBasePropertiesSevice
     *
     */
    @Bean
    ApplicationBasePropertiesSeviceImpl applicationBasePropertiesSeviceImpl() {
	return new ApplicationBasePropertiesSeviceImpl();
    }

    /*
     * Serve per parametrizzare l'applicazione specifica per esempio per caricare le variabili di
     * sistema che hanno come suffisso ad esempio "saceriam".
     */
    @Bean
    String nomeApplicazione() {
	return "sacerping";
    }

    /*
     * CONFIGURAZIONE DEI BEAN DELLE ACTION che prima erano nell'xml di springweb Configurazione
     * delle action ereditate dal framework
     */
    @Bean(value = "/View.html")
    RedirectAction redirectAction() {
	return new RedirectAction();
    }

    @Bean(value = "/Login.html")
    LoginAction loginAction() {
	return new LoginAction();
    }

    @Bean(value = "/Logout.html")
    LogoutAction logoutAction() {
	return new LogoutAction();
    }

    /* Configurazione delle action specifiche del modulo web */
    @Bean(value = "/Home.html")
    HomeAction homeAction() {
	return new HomeAction();
    }

    @Bean(value = "/SceltaOrganizzazione.html")
    SceltaOrganizzazioneAction sceltaOrganizzazioneAction() {
	return new SceltaOrganizzazioneAction();
    }

    /* Action specifiche di sacerping */

    @Bean(value = "/Amministrazione.html")
    AmministrazioneAction amministrazioneAction() {
	return new AmministrazioneAction();
    }

    @Bean(value = "/Monitoraggio.html")
    MonitoraggioAction monitoraggioAction() {
	return new MonitoraggioAction();
    }

    @Bean(value = "/GestioneJob.html")
    GestioneJobAction gestioneJobAction() {
	return new GestioneJobAction();
    }

    @Bean(value = "/VersamentoOggetto.html")
    VersamentoOggettoAction versamentoOggettoAction() {
	return new VersamentoOggettoAction();
    }

    @Bean(value = "/EntiConvenzionati.html")
    EntiConvenzionatiAction entiConvenzionatiAction() {
	return new EntiConvenzionatiAction();
    }

    /** xFormer **/

    @Bean(value = "/Trasformazioni.html")
    TrasformazioniAction trasformazioniAction() {
	return new TrasformazioniAction();
    }

    @Bean(value = "/RicercaTrasformazioni.html")
    TrasformazioniAction ricercaTrasformazioniAction() {
	return new TrasformazioniAction();
    }

    @Bean(value = "/MonitoraggioServerTrasformazioni.html")
    TrasformazioniAction monitoraggioServerTrasformazioni() {
	return new TrasformazioniAction();
    }

    @Bean(value = "/StrumentiUrbanistici.html")
    StrumentiUrbanisticiAction strumentiUrbanisticiAction() {
	return new StrumentiUrbanisticiAction();
    }

    @Bean(value = "/Sisma.html")
    SismaAction sismaAction() {
	return new SismaAction();
    }

}
