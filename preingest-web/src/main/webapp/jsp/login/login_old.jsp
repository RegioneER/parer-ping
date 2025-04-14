<%@page import="it.eng.spagoCore.ConfigSingleton"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Autenticazione - Sacer" />
    <sl:body>
        <sl:header description="Autenticazione - Sacer" showHomeBtn="false"/>
        <sl:newLine skipLine="true"/>
        <div class="logo"><img src="<c:url value="/img/sacer_ultimate.jpg" />" alt="Sacer" class="logoImg" /></div>

        
        <div id="content">
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <form  method="post" action="<c:url value="./Login.html"/>"  >


                <div id="contenuto">

                    <br />
                    <div class="w60 modulo">
                        <fieldset>
                            <legend>Autenticazione</legend>
                            <div class="containerLeft w50">

                                <div class="newLine skipLine"></div>
                                I campi sono tutti obbligatori<br />
                                <br />

                                <div>
                                    <input name="operation" type="hidden" id="operation" value="passIn"/>

                                    <!--username-->
                                    <label class="slLabel w40" for="txtUsername">Username:&nbsp;</label>
                                    <input name="username" class="slText w40" type="text" accesskey="U" id="txtUsername"/>

                                    <div class="newLine skipLine"></div>
                                    <!-- vecchia password-->
                                    <label class="slLabel w40" for="txtOldPassword" accesskey="P" >Password:&nbsp;</label>
                                    <input name="password" class="slText w40" type="password" id="txtOldPassword"/>

                                    <div class="newLine skipLine"></div>

                                    <div class="pulsantiera">

                                        <input type="submit" name="Login" value="Accedi" accesskey="A" /><br />
                                        <br />
                                    </div>
                                </div>
                            </div>
                        </fieldset>

                    </div>
                </div>                    

            </form>
        </div>
        <!--Footer-->
        <sl:footer />
    </sl:body>
</sl:html>
