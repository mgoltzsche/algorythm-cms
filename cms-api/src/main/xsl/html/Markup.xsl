<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://cms.algorythm.de/common/CMS"
	xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="c">
	<xsl:param name="relativeBaseURL" />

	<xsl:template match="c:p">
		<xsl:if test="*|text()">
			<p>
				<xsl:apply-templates />
			</p>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="c:b">
		<b>
			<xsl:apply-templates />
		</b>
	</xsl:template>
	
	<xsl:template match="c:i">
		<i>
			<xsl:apply-templates />
		</i>
	</xsl:template>
	
	<xsl:template match="c:br">
		<br />
	</xsl:template>
	
	<xsl:template match="c:h1 | c:h2 | c:h3 | c:h4 | c:h5 | c:h6 | c:h7">
		<xsl:if test="*|text()">
			<xsl:element name="{local-name()}">
				<xsl:apply-templates />
			</xsl:element>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="c:ul | c:ol">
		<xsl:element name="{local-name()}">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="c:li">
		<li>
			<xsl:apply-templates />
		</li>
	</xsl:template>
	
	<xsl:template match="c:position">
		<xsl:variable name="position">
			<xsl:if test="@left">
				<xsl:text disable-output-escaping="yes">left:</xsl:text>
				<xsl:value-of select="@left" />
				<xsl:text disable-output-escaping="yes">;</xsl:text>
			</xsl:if>
			<xsl:if test="@right">
				<xsl:text disable-output-escaping="yes">right:</xsl:text>
				<xsl:value-of select="@right" />
				<xsl:text disable-output-escaping="yes">;</xsl:text>
			</xsl:if>
			<xsl:if test="@top">
				<xsl:text disable-output-escaping="yes">top:</xsl:text>
				<xsl:value-of select="@top" />
				<xsl:text disable-output-escaping="yes">;</xsl:text>
			</xsl:if>
			<xsl:if test="@bottom">
				<xsl:text disable-output-escaping="yes">bottom:</xsl:text>
				<xsl:value-of select="@bottom" />
				<xsl:text disable-output-escaping="yes">;</xsl:text>
			</xsl:if>
		</xsl:variable>
		<div style="position: absolute; {$position}">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="c:spacer">
		<div class="spacer">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="c:a">
		<a href="{@href}" title="{@title}">
			<xsl:apply-templates />
		</a>
	</xsl:template>
	
	<xsl:template match="c:img">
		<xsl:variable name="fill" select="if (@fill) then boolean(@fill) else false()" />
		<xsl:choose>
			<xsl:when test="$fill">
				<img src="{$relativeBaseURL}{@src}" alt="{@title}" class="img-fill" />
			</xsl:when>
			<xsl:otherwise>
				<div class="img clearfix">
					<div>
						<img src="{$relativeBaseURL}{@src}" alt="" cms-image-dialog="{@title}" />
						<span>
							<xsl:value-of select="@title" />
						</span>
					</div>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="c:file">
		<a href="{$relativeBaseURL}{@src}">
			<xsl:value-of select="@title" />
		</a>
	</xsl:template>
	
	<xsl:template match="c:code">
		<pre>
			<xsl:apply-templates />
		</pre>
	</xsl:template>
	
	<xsl:template match="c:properties">
		<table class="properties-table">
			<xsl:if test="@title">
				<caption><xsl:value-of select="@title" /></caption>
			</xsl:if>
			<xsl:for-each select="*">
				<tr>
					<th><xsl:value-of select="@name" />: </th>
					<td>
						<xsl:apply-templates />
					</td>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template match="c:bot-secure">
		<span cms-bot-safe="true">
			<xsl:call-template name="c:string2hex">
				<xsl:with-param name="str" select="@value" />
			</xsl:call-template>
		</span>
	</xsl:template>
	
	<xsl:variable name="ascii"> !"#$%&amp;'()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~</xsl:variable>
	<xsl:variable name="hex" >0123456789ABCDEF</xsl:variable>
	
	<xsl:template name="c:string2hex">
		<xsl:param name="str"/>
		<xsl:if test="$str">
			<xsl:variable name="first-char" select="substring($str,1,1)"/>
			<xsl:variable name="ascii-value" select="string-length(substring-before($ascii,$first-char)) + 32"/>
			<xsl:variable name="hex-digit1" select="substring($hex,floor($ascii-value div 16) + 1,1)"/>
			<xsl:variable name="hex-digit2" select="substring($hex,$ascii-value mod 16 + 1,1)"/>
			<xsl:value-of select="concat('x', $hex-digit1,$hex-digit2)"/>
			<xsl:if test="string-length($str) gt 1">
				<xsl:call-template name="c:string2hex">
					<xsl:with-param name="str" select="substring($str,2)"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
