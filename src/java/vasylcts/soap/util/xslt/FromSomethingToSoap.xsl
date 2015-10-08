<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" version="1.0">
    <xsl:param name="namespace"/>
    <xsl:param name="responseElementName"/>
    <xsl:param name="undefinedResponseTagParent"/>
    <xsl:param name="undefinedResponseTagChild"/>
    <xsl:output method="xml" omit-xml-declaration="no" indent="no"/> 
    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    
    <xsl:template match="/">
        <xsl:element name="S:Envelope" namespace="http://schemas.xmlsoap.org/soap/envelope/">
            <xsl:element name="S:Body">
                <xsl:element name="ns2:{$responseElementName}" namespace="{$namespace}">
                    <xsl:apply-templates/>
                </xsl:element>    
            </xsl:element>
        </xsl:element> 
    </xsl:template>

    <xsl:template match="//root">
        <xsl:choose>
            <xsl:when test="$undefinedResponseTagParent = ''">
                <xsl:copy-of select="//root/*"/>
            </xsl:when>  
            <xsl:when test="($undefinedResponseTagParent = 'typeCDATA') or ($undefinedResponseTagParent = 'typeXML') or ($undefinedResponseTagParent = 'typeBASE64')">
                <xsl:copy-of select="//root/additionalInfo"/>
                <xsl:element name="{$undefinedResponseTagParent}">
                    <xsl:element name="{$undefinedResponseTagChild}">
                        <xsl:text>&lt;![CDATA[</xsl:text>
                        <xsl:copy-of select="//root/*[not(self::additionalInfo)]"/>
                        <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
                    </xsl:element>
                </xsl:element> 
            </xsl:when>  
            <xsl:otherwise>
                <xsl:copy-of select="//root/additionalInfo"/>
                <xsl:element name="{$undefinedResponseTagParent}">
                    <xsl:element name="{$undefinedResponseTagChild}">
                       <xsl:copy-of select="//root/*[not(self::additionalInfo)]"/>
                    </xsl:element>
                </xsl:element>                                 
            </xsl:otherwise>                      
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
