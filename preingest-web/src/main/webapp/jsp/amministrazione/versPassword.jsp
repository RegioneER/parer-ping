<%@ page import="it.eng.sacerasi.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Inserisci password versatore" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="INSERISCI PASSWORD" />
            <slf:fieldBarDetailTag name="<%= AmministrazioneForm.EditPasswordVersatore.NAME%>" hideOperationButton="true"/> 
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <label class="slLabel wlbl" for="Cd_psw_vers">Password versatore</label>
                <div class="containerLeft w2ctr">
                    <input id="Cd_psw_vers" class="slText w60" type="password" value="" name="Cd_psw_vers" />
                </div>
                <sl:newLine skipLine="true"/>
                <label class="slLabel wlbl" for="Cd_psw_vers_repeated">Conferma password</label>
                <div class="containerLeft w2ctr">
                    <input id="Cd_psw_vers_repeated" class="slText w60" type="password" value="" name="Cd_psw_vers_repeated" />
                </div>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=AmministrazioneForm.EditPasswordVersatore.SALVA_PASSWORD_VERS%>" width="w25"/>
            </sl:pulsantiera>
            
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <label class="slLabel wlbl" for="Cd_psw_sacer">Password sacer</label>
                <div class="containerLeft w2ctr">
                    <input id="Cd_psw_sacer" class="slText w60" type="password" value="" name="Cd_psw_sacer" />
                </div>
                <sl:newLine skipLine="true"/>
                <label class="slLabel wlbl" for="Cd_psw_sacer_repeated">Conferma password</label>
                <div class="containerLeft w2ctr">
                    <input id="Cd_psw_sacer_repeated" class="slText w60" type="password" value="" name="Cd_psw_sacer_repeated" />
                </div>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=AmministrazioneForm.EditPasswordSacer.SALVA_PASSWORD_SACER %>" width="w25"/>
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
