<%@page import="java.util.Date"%>
<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoCore.ConfigSingleton"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Cambio Password - Sacer" />
    <sl:body>
        <sl:header description="Cambio Password" showHomeBtn="${sessionScope.scaduto == null}"/>
       
        <div class="newLine "></div>

        <c:choose>
            <c:when test="${sessionScope.scaduto != null}">
                <div class="logo"><!--<img src="<c:url value="/img/sacer_ultimate.jpg" />" alt="Sacer" class="logoImg" />-->
                </div>          
            </c:when>
            <c:otherwise>
                <sl:menu showChangePasswordBtn="true" />
            </c:otherwise>
        </c:choose>
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="CAMBIO PASSWORD"/>
            <form method="post" action="./Login.html"  >

                <c:choose>
                    <c:when test="${sessionScope.scaduto != null}">
                        <div class="listToolBar">
                            <a class="navBarBackLink" href="Login.html" title="Indietro">Indietro</a>
                        </div>       
                    </c:when>
                    <c:otherwise>
                        <div class="listToolBar">
                            <a class="navBarBackLink" href="Home.html" title="Indietro">Indietro</a>
                        </div>
                    </c:otherwise>
                </c:choose>              

                <div id="contenuto">
                    <br />
                    <div class="w80 modulo">
                        <fieldset class="noborder">
                            <legend>Cambio password</legend>
                            <div class="containerLeft w50">

                                <div class="newLine skipLine"></div>
                                I campi sono tutti obbligatori<br />
                                <br />

                                <div>
                                    <input name="operation" type="hidden" id="operation" value="changePwd"/>


                                    <!--vecchia password-->
                                    <label class="slLabel w40" for="txtUsername">Vecchia password:&nbsp;</label>
                                    <input class="slText w40" name="oldpass" type="password" accesskey="U" id="oldpass"/>

                                    <div class="newLine skipLine"></div>
                                    <!-- nuova password-->
                                    <label class="slLabel w40" for="txtOldPassword" accesskey="P" >Nuova password:&nbsp;</label>
                                    <input class="slText w40" name="newpass" type="password" id="newpass"/>

                                    <div class="newLine skipLine"></div>
                                    <!-- conferma nuova password-->
                                    <label class="slLabel w40" for="txtOldPassword" accesskey="C" >Conferma nuova password:&nbsp;</label>
                                    <input class="slText w40" name="confnewpass" type="password" id="newpass2"/>

                                    <div class="newLine skipLine"></div>

                                    <div class="pulsantiera">
                                        <!--a id="modificaPassword" name="#modificaPassword"></a-->
                                        <input type="submit" name="cmdModificaPassword" value="Modifica" accesskey="A" /><br />
                                        <br />
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                        Regole per la gestione dalla password:
                        <ul>
                            <li>
                                utilizzare una password lunga almeno 8 caratteri che contenga almeno un numero;
                            </li>
                            <li>
                                modificare la password almeno ogni 90 giorni;
                            </li>
                            <li>
                                modificare la password nel caso in cui si sospetti che chiunque altro ne sia
                                venuto a conoscenza;
                            </li>
                            <li>
                                scegliere la password in modo che non sia collegata alla propria vita privata
                                (per es. il nome o il cognome di familiari, la targa dell'auto, la data di
                                nascita, la citt&agrave;  di residenza, ecc);
                            </li>
                            <li>
                                non scegliere come password parole comuni riportate in un vocabolario
                                (facilmente attaccabili da software di password cracking);
                            </li>
                            <li>
                                &Egrave; infine consigliato scegliere una password che contenga combinazioni di
                                lettere maiuscole e minuscole, numeri, caratteri speciali (per es. !, *, /, ?,
                                #).</li>
                        </ul>
                    </div>
                </div>                    

            </form>
        </sl:content>

        <!--Footer-->
        <sl:footer />           

    </sl:body>
</sl:html>
