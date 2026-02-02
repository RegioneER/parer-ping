<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@page import="it.eng.spagoCore.ConfigSingleton"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Autenticazione - Ping" />
    <sl:body>
        <div class="header">
            <a title="ParER - Polo Archivistico Regionale dell'Emilia-Romagna" href="https://poloarchivistico.regione.emilia-romagna.it/">
                <img alt="Logo parer" src="<c:url value='/img/regione/LogoParer.png' />" class="floatRight" />
            </a><div class="newLine">

            </div></div>
            <sl:newLine skipLine="true"/>
            <slf:messageBox />

        <div id="loginPage" class="center">
            <img src="<c:url value='/img/logo_sacer.png' />" alt="Sacer" class="logoImg" />

                 <form  method="post" action="<c:url value="./Login.html"/>"  >
                 <div class="newLine skipLine"></div>

                <fieldset>


                    <div class="loginForm">
                        <span>Autenticazione</span>
                        <div class="newLine skipLine"></div>
                        <div class="containerLeft">I campi sono tutti obbligatori</div>
                        <div class="newLine skipLine"></div>


                        <input name="operation" type="hidden" id="operation" value="passIn"/>

                        <!--username-->
                        <label class=" w40" for="txtUsername">Username:&nbsp;</label>
                        <input name="username" class=" w40" type="text" accesskey="U" id="txtUsername"/>

                        <div class="newLine skipLine"></div>
                        <!-- vecchia password-->
                        <label class=" w40" for="txtOldPassword" accesskey="P" >Password:&nbsp;</label>
                        <input name="password" class=" w40" type="password" id="txtOldPassword"/>
                        <div class="newLine skipLine"></div>                   
                    </div>  
                    <div class="newLine skipLine"></div>

                    <div class="floatRight pulsantiera">
                        <input type="submit" name="Login" value="Accedi" accesskey="A" />
                    </div>

                </fieldset>
            </form>
        </div>

        <!--Footer-->
        <sl:footer />
    </sl:body>
</sl:html>
