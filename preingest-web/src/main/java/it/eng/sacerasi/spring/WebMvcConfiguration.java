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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

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
                .setCachePeriod(3600); // Cache for 3600 seconds for better performance
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
    public PaginatorImpl paginatorImpl() {
        return new PaginatorImpl();
    }

    /*
     * Classe che va a caricare le autorizzazioni da IAM
     */
    @Bean(name = "authenticator")
    public PreingestAuthenticator preingestAuthenticator() {
        return new PreingestAuthenticator();
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(30000);
        factory.setConnectTimeout(15000);
        factory.setBufferRequestBody(false); // Added for better performance with large requests
        return new RestTemplate(factory);
    }

    /*
     * Template da inserire nelle applicazioni che usano SpagoLite e che utilizzano l' Help On line.
     * Deve implementare l'interfaccia IApplicationBasePropertiesSevice
     *
     */
    @Bean
    public ApplicationBasePropertiesSeviceImpl applicationBasePropertiesSeviceImpl() {
        return new ApplicationBasePropertiesSeviceImpl();
    }

    /*
     * Serve per parametrizzare l'applicazione specifica per esempio per caricare le variabili di
     * sistema che hanno come suffisso ad esempio "saceriam".
     */
    @Bean
    public String nomeApplicazione() {
        return "sacerping";
    }

    /*
     * CONFIGURAZIONE DEI BEAN DELLE ACTION che prima erano nell'xml di springweb Configurazione
     * delle action ereditate dal framework
     */
    @Bean(name = "/View.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public RedirectAction redirectAction() {
        return new RedirectAction();
    }

    @Bean(name = "/Login.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public LoginAction loginAction() {
        return new LoginAction();
    }

    @Bean(name = "/Logout.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public LogoutAction logoutAction() {
        return new LogoutAction();
    }

    /* Configurazione delle action specifiche del modulo web */
    @Bean(name = "/Home.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public HomeAction homeAction() {
        return new HomeAction();
    }

    @Bean(name = "/SceltaOrganizzazione.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public SceltaOrganizzazioneAction sceltaOrganizzazioneAction() {
        return new SceltaOrganizzazioneAction();
    }

    /* Action specifiche di sacerping */

    @Bean(name = "/Amministrazione.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public AmministrazioneAction amministrazioneAction() {
        return new AmministrazioneAction();
    }

    @Bean(name = "/Monitoraggio.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public MonitoraggioAction monitoraggioAction() {
        return new MonitoraggioAction();
    }

    @Bean(name = "/GestioneJob.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public GestioneJobAction gestioneJobAction() {
        return new GestioneJobAction();
    }

    @Bean(name = "/VersamentoOggetto.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public VersamentoOggettoAction versamentoOggettoAction() {
        return new VersamentoOggettoAction();
    }

    @Bean(name = "/EntiConvenzionati.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public EntiConvenzionatiAction entiConvenzionatiAction() {
        return new EntiConvenzionatiAction();
    }

    /* xFormer */

    @Bean(name = "/Trasformazioni.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public TrasformazioniAction trasformazioniAction() {
        return new TrasformazioniAction();
    }

    @Bean(name = "/RicercaTrasformazioni.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public TrasformazioniAction ricercaTrasformazioniAction() {
        return new TrasformazioniAction();
    }

    @Bean(name = "/MonitoraggioServerTrasformazioni.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public TrasformazioniAction monitoraggioServerTrasformazioni() {
        return new TrasformazioniAction();
    }

    @Bean(name = "/StrumentiUrbanistici.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public StrumentiUrbanisticiAction strumentiUrbanisticiAction() {
        return new StrumentiUrbanisticiAction();
    }

    @Bean(name = "/Sisma.html")
    @Scope(WebApplicationContext.SCOPE_REQUEST)
    public SismaAction sismaAction() {
        return new SismaAction();
    }

}
