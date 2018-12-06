/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.radcentre.data.dao;

public class CaseDao {
	private String billingMethod;
	private String costBearer;
	private String insuranceNumber;
	private String accidentDate;
	private String accidentNumber;
	private String ivNumber;
	private String invoiceRecipient;
	private String referrer;

	public String getBillingMethod() {
		return billingMethod;
	}

	public String getCostBearer() {
		return costBearer;
	}

	public String getInsuranceNumber() {
		return insuranceNumber;
	}

	public String getAccidentDate() {
		return accidentDate;
	}

	public String getAccidentNumber() {
		return accidentNumber;
	}
	
	public String getIvNumber() {
		return ivNumber;
	}

	public String getInvoiceRecipient() {
		return invoiceRecipient;
	}
	
	public String getReferrer() {
		return referrer;
	}
	
	public void setBillingMethod(String billingMethod) {
		this.billingMethod = billingMethod;
	}

	public void setCostBearer(String costBearer) {
		this.costBearer = costBearer;
	}

	public void setInsuranceNumber(String insuranceNumber) {
		this.insuranceNumber = insuranceNumber;
	}

	public void setAccidentDate(String accidentDate) {
		this.accidentDate = accidentDate;
	}

	public void setAccidentNumber(String accidentNumber) {
		this.accidentNumber = accidentNumber;
	}
	
	public void setIvNumber(String ivNumber) {
		this.ivNumber = ivNumber;
	}

	public void setInvoiceRecipient(String invoiceRecipient) {
		this.invoiceRecipient = invoiceRecipient;
	}
	
	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}
}
