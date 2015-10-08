<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" version="1.0">
    <xsl:param name="faultStringParam"/>
    <xsl:param name="detailParam"/>
    <xsl:param name="isServerFault"/>
    
    <xsl:output method="xml" omit-xml-declaration="no" indent="no"/> 

    <xsl:template match="/">
        <xsl:element name="S:Envelope" namespace="http://schemas.xmlsoap.org/soap/envelope/">
            <xsl:element name="S:Body">
                <xsl:element name="S:Fault">
                    <xsl:element name="faultcode">
                        <xsl:choose> 
                            <xsl:when test="$isServerFault = 1">
                                <xsl:text>S:Server</xsl:text>
                            </xsl:when>  
                            <xsl:otherwise>
                                <xsl:text>S:Client</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>     
                    </xsl:element>    
                    <xsl:element name="faultstring"> 
                        <xsl:text>&lt;![CDATA[</xsl:text>
                        <xsl:value-of select="$faultStringParam"/>
                        <xsl:text>]]&gt;</xsl:text>
                    </xsl:element>    
                    <xsl:element name="detail"> 
                        <xsl:choose> 
                            <xsl:when test="$detailParam != ''">
                                <xsl:element name="detailString"> 
                                    <xsl:text>&lt;![CDATA[</xsl:text>
                                    <xsl:value-of select="$detailParam"/>
                                    <xsl:text>]]&gt;</xsl:text>
                                </xsl:element> 
                            </xsl:when>
                        </xsl:choose>     
                        <xsl:copy-of select="/rootTempElement/*"/>
                    </xsl:element> 
                </xsl:element>    
            </xsl:element>
        </xsl:element> 
    </xsl:template>

</xsl:stylesheet>
