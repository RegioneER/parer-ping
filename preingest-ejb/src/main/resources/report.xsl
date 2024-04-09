<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exslt="http://exslt.org/common">
    <!-- -->
    <xsl:template match="/Report">
        <xsl:element name="ReportTrasformazione">
            <!-- elements 'Testata' -->
            <xsl:element name="Testata">
                <xsl:element name="Versatore">
                    <xsl:value-of select="./Versatore" />
                </xsl:element>
                <xsl:element name="VersatoreDiDestinazione">
                    <xsl:value-of select="./VersatoreDiDestinazione" />
                </xsl:element>
                <xsl:element name="StrutturaDiDestinazione">
                    <xsl:element name="Ambiente">
                        <xsl:value-of select="./Ambiente" />
                    </xsl:element>
                    <xsl:element name="Ente">
                        <xsl:value-of select="./Ente" />
                    </xsl:element>
                    <xsl:element name="Struttura">
                        <xsl:value-of select="./Struttura" />
                    </xsl:element>
                </xsl:element>
            </xsl:element>
            <!-- elements 'OggettoDaTrasfomare' -->
            <xsl:element name="OggettoDaTrasfomare">
                <xsl:element name="NomeOggetto">
                    <xsl:value-of select="./NomeOggetto" />
                </xsl:element>
                <xsl:element name="DettaglioOggetto">
                    <!-- -->
                    <xsl:attribute name="pigId">
                        <xsl:choose>
                            <xsl:when test="./@pigId and ./@pigId!= ''">
                                <xsl:value-of select="./@pigId" />
                            </xsl:when>
                            <xsl:when test="./IdPigObject and ./IdPigObject!= ''">
                                <xsl:value-of select="./IdPigObject" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>UNKNOWN</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <!-- -->
                    <xsl:apply-templates select="./@pigId" />
                    <!-- -->
                    <xsl:element name="TipoOggetto">
                        <xsl:value-of select="./TipoOggetto" />
                    </xsl:element>
                    <xsl:element name="DimensioneOggetto">
                        <xsl:value-of select="./DimensioneOggetto" />
                    </xsl:element>
                    <!-- -->
                    <xsl:if test="count(./SommarioTrasformazione/ContenutoOggetto[count(./*)&gt;0])&gt;0">
                        <xsl:element name="ContenutoOggetto">
                            <xsl:for-each select="./SommarioTrasformazione/ContenutoOggetto[./NumeroFileEstratti]">
                                <xsl:apply-templates select="./*" />
                            </xsl:for-each>
                            <xsl:for-each select="./SommarioTrasformazione/ContenutoOggetto[not(./NumeroFileEstratti)]">
                                <xsl:apply-templates select="./*" />
                            </xsl:for-each>
                        </xsl:element>
                    </xsl:if>
                    <!-- -->
                </xsl:element>
                <!-- -->
            </xsl:element>
            <!-- elements 'Trasformazione' -->
            <xsl:element name="Trasformazione">
                <!-- -->
                <xsl:element name="NomeTrasformazione">
                    <xsl:value-of select="./NomeTrasformazione" />
                </xsl:element>
                <!-- -->
                <xsl:element name="Esito">
                    <xsl:value-of select="./Esito" />
                </xsl:element>
                <!-- -->
                <xsl:element name="DataInizio">
                    <xsl:value-of select="./DataInizio" />
                </xsl:element>
                <xsl:element name="DataFine">
                    <xsl:value-of select="./DataFine" />
                </xsl:element>
                <!-- -->
                <xsl:element name="DettaglioTrasformazione">
                    <!-- -->
                    <xsl:element name="VersioneTrasformazione">
                        <xsl:value-of select="./VersioneTrasformazione" />
                    </xsl:element>
                    <xsl:element name="IdTrasformazione">
                        <xsl:value-of select="./IdTrasformazione" />
                    </xsl:element>
                    <!-- -->
                    <!-- <xsl:apply-templates select="./ParametriTrasformazione" /> -->
                    <xsl:element name="ParametriTrasformazione">
                        <xsl:choose>
                            <xsl:when test="function-available('exslt:node-set')">
                                <!-- try to use variable and exslt support -->
                                <xsl:variable name="_ParametriTrasformazione">
                                    <xsl:call-template name="parameter_splitter">
                                        <xsl:with-param name="testo_da_analizzare" select="./ParametriTrasformazione" />
                                        <xsl:with-param name="separatore" select="' | '" />
                                    </xsl:call-template>
                                </xsl:variable>
                                <xsl:for-each select="exslt:node-set($_ParametriTrasformazione)/parametro">
                                    <xsl:sort select="./@nome" data-type="text" order="ascending" case-order="upper-first"/> 
                                    <xsl:copy-of select="." /> 
                                </xsl:for-each>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:call-template name="parameter_splitter">
                                    <xsl:with-param name="testo_da_analizzare" select="./ParametriTrasformazione" />
                                    <xsl:with-param name="separatore" select="' | '" />
                                </xsl:call-template>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                    <!-- -->
                </xsl:element>
                <!-- -->
                <!-- <xsl:apply-templates select="./NumeroOggettiFigli" /> -->
                <xsl:element name="OggettiFigliGenerati">
                    <xsl:value-of select="./NumeroOggettiFigli" />
                </xsl:element>
                <!-- -->
                <!-- UnitaDocumentarie -->
                <xsl:element name="UnitaDocumentarie">
                    <!-- -->
                    <xsl:apply-templates select="./SommarioTrasformazione/UnitaDocumentarie[./NumeroUD][1]/*" />
                    <!-- -->
                </xsl:element>
                <!-- -->
                <!-- elements 'AltreInformazioni' -->
                <xsl:element name="AltreInformazioni">
                    <xsl:for-each select="./*">
                        <xsl:apply-templates select="." />
                    </xsl:for-each>
                </xsl:element>
                <!-- elements 'ListaErrori' -->
                <xsl:element name="ListaErrori">
                    <xsl:for-each select="./Errori/Errore[not(contains(., 'warning'))]">
                        <xsl:apply-templates select="." />
                    </xsl:for-each>
                </xsl:element>
                <!-- elements 'ListaWarning' -->
                <xsl:element name="ListaWarning">
                    <xsl:for-each select="./Errori/Errore[contains(., 'warning')]">
                        <xsl:apply-templates select="." />
                    </xsl:for-each>
                </xsl:element>
                <!-- -->
            </xsl:element>
            <!-- 
           <xsl:for-each select="./*">
                   <xsl:apply-templates select="." />
           </xsl:for-each>
            -->
            <!-- -->
        </xsl:element>
    </xsl:template>
    <!-- -->
    <!-- start skip elements 'Testata' -->
    <xsl:template match="/Report/Versatore"></xsl:template>
    <xsl:template match="/Report/VersatoreDiDestinazione"></xsl:template>
    <xsl:template match="/Report/Ambiente"></xsl:template>
    <xsl:template match="/Report/Ente"></xsl:template>
    <xsl:template match="/Report/Struttura"></xsl:template>
    <!-- end skip elements 'Testata' -->
    <!-- start skip elements 'SommarioTrasformazione' -->
    <xsl:template match="/Report/SommarioTrasformazione"></xsl:template>
    <!-- end skip elements 'SommarioTrasformazione' -->
    <!-- start skip elements 'OggettoDaTrasfomare' -->
    <xsl:template match="/Report/NomeOggetto"></xsl:template>
    <xsl:template match="/Report/IdPigObject"></xsl:template>
    <xsl:template match="/Report/TipoOggetto"></xsl:template>
    <xsl:template match="/Report/DimensioneOggetto"></xsl:template>
    <!-- end skip elements 'OggettoDaTrasfomare' -->
    <!-- start skip elements 'Trasformazione' -->
    <xsl:template match="/Report/NomeTrasformazione"></xsl:template>
    <xsl:template match="/Report/Esito"></xsl:template>
    <xsl:template match="/Report/DataInizio"></xsl:template>
    <xsl:template match="/Report/DataFine"></xsl:template>
    <xsl:template match="/Report/VersioneTrasformazione"></xsl:template>
    <xsl:template match="/Report/IdTrasformazione"></xsl:template>
    <xsl:template match="/Report/ParametriTrasformazione"></xsl:template>
    <xsl:template match="/Report/NumeroOggettiFigli"></xsl:template>
    <xsl:template match="/Report/Errori"></xsl:template>
    <!-- end skip elements 'Trasformazione' -->

    <!-- -->
    <xsl:template match="@*|node()" priority="-1">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    <!-- -->
    <xsl:template name="parameter_splitter">
        <xsl:param name="testo_da_analizzare" select="''" />
        <xsl:param name="separatore" select="''" />
        <!-- -->
        <xsl:if test="normalize-space($testo_da_analizzare) != ''">
            <!-- -->
            <xsl:variable name="current_parameter">
                <xsl:choose>
                    <xsl:when test="contains(normalize-space($testo_da_analizzare), $separatore)">
                        <xsl:value-of select="substring-before(normalize-space($testo_da_analizzare), $separatore)" />
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="normalize-space($testo_da_analizzare)" />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <!-- -->
            <xsl:if test="contains(normalize-space($current_parameter), ' : ')">
                <xsl:element name="parametro">
                    <xsl:attribute name="nome">
                        <xsl:value-of select="substring-before(normalize-space($current_parameter), ' : ')" />
                    </xsl:attribute>
                    <xsl:value-of select="substring-after(normalize-space($current_parameter), ' : ')" />
                </xsl:element>
            </xsl:if>
            <!-- -->
            <xsl:if test="contains(normalize-space($testo_da_analizzare), $separatore)">
                <xsl:call-template name="parameter_splitter">
                    <xsl:with-param name="testo_da_analizzare" select="substring-after(normalize-space($testo_da_analizzare), $separatore)" />
                    <xsl:with-param name="separatore" select="$separatore" />
                </xsl:call-template>
            </xsl:if>
            <!-- -->
        </xsl:if>
        <!-- -->
    </xsl:template>
    <!-- -->
    <xsl:output method="xml" version="1.0" indent="yes" encoding="UTF-8" />
    <!-- -->
</xsl:stylesheet>
