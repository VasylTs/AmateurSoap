<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:as="http://www.somemyrandomsitewiththissoap.com.ua/AmauterSoap" version="1.0">
    <xsl:param name="actionName"/>
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/> 
     <xsl:template match="/">    
        <xsl:element name="someParentTag">
            <xsl:template match="//S:Envelope/S:Body/*[1]/as:additionalInfo">    
                <xsl:element name="Action">
                    <xsl:value-of select="$actionName"/>
                </xsl:element>
                <xsl:for-each select="//S:Envelope/S:Body/*[1]/as:additionalInfo/*">
                    <xsl:variable name="ename" select="name()"/>
                    <xsl:element name="{$ename}New">
                        <xsl:value-of select="."/>
                    </xsl:element> 
                </xsl:for-each>  
            </xsl:template>
            <xsl:template match="//S:Envelope/S:Body/*[1]/*[not(self::as:additionalInfo)][1]">
                <xsl:element name="aditionalParams">
                    <xsl:for-each select="//S:Envelope/S:Body/*[1]/*[not(self::as:additionalInfo)]">
                            <xsl:variable name="ename" select="name()"/>
                            <xsl:variable name="xt" select="local-name(*[1])"/>
                            <xsl:choose>
                                <xsl:when test="($xt = 'typeSimpleString') or ($xt = 'typeCDATAString') or ($xt = 'typeBASE64String') or ($ename = 'typeXMLString')">
                                    <xsl:element name="additionalParam">
                                        <xsl:attribute name="paramName"><xsl:value-of select="$ename"/></xsl:attribute>
                                        <xsl:attribute name="paramType"><xsl:value-of select="$xt"/></xsl:attribute>
                                        <xsl:value-of select="."/>
                                    </xsl:element>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:element name="additionalParam">
                                        <xsl:attribute name="paramName"><xsl:value-of select="$ename"/></xsl:attribute>
                                        <xsl:value-of select="."/>
                                    </xsl:element>  
                                </xsl:otherwise>
                            </xsl:choose>
                    </xsl:for-each>    
                </xsl:element>
            </xsl:template>
        </xsl:element>
    </xsl:template>      
</xsl:stylesheet>
