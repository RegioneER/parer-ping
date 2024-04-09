<%@page import="it.eng.spagoCore.configuration.ConfigSingleton"%>
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
