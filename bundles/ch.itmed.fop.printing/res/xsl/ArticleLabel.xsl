<?xml version="1.0" encoding="utf-8"?>
<!-- (c) IT-Med AG 2019; All rights reserved -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" indent="yes" />
	<xsl:variable name="pageWidth">
		<xsl:value-of select="Page/@pageWidth" />
	</xsl:variable>
	<xsl:variable name="pageHeight">
		<xsl:value-of select="Page/@pageHeight" />
	</xsl:variable>
	<xsl:variable name="marginTop">
		<xsl:value-of select="Page/@marginTop" />
	</xsl:variable>
	<xsl:variable name="marginBottom">
		<xsl:value-of select="Page/@marginBottom" />
	</xsl:variable>
	<xsl:variable name="marginLeft">
		<xsl:value-of select="Page/@marginLeft" />
	</xsl:variable>
	<xsl:variable name="marginRight">
		<xsl:value-of select="Page/@marginRight" />
	</xsl:variable>
	<xsl:variable name="textOrientation">
		<xsl:value-of select="Page/@textOrientation" />
	</xsl:variable>
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master
					master-name="ArticleLabel" page-width="{$pageWidth}"
					page-height="{$pageHeight}" margin-top="{$marginTop}"
					margin-bottom="{$marginBottom}" margin-left="{$marginLeft}"
					margin-right="{$marginRight}">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="ArticleLabel">
				<fo:flow flow-name="xsl-region-body"
					reference-orientation="{$textOrientation}">
					<fo:block-container font="8pt Helvetica"
						font-weight="normal" text-align="center">
						<xsl:apply-templates />
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="Articles">
		<xsl:for-each select="Article">
			<fo:block-container page-break-after="always">
				<fo:block>
					<xsl:value-of select="/Page/Patient/Salutation" />
					&#160;
					<xsl:value-of select="/Page/Patient/FirstName" />
					&#160;
					<xsl:value-of select="/Page/Patient/LastName" />
					,&#160;
					<xsl:value-of select="/Page/Patient/Birthdate" />
				</fo:block>
				<fo:block>
					<fo:leader />
				</fo:block>
				<fo:block font-style="italic">
					<xsl:value-of select="Name" />
				</fo:block>
				<fo:block>
					Abgabedatum:&#160;
					<xsl:value-of select="DeliveryDate" />
				</fo:block>
				<fo:block font-weight="bold">
					Preis:&#160;CHF&#160;
					<xsl:value-of select="Price" />
				</fo:block>
			</fo:block-container>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>