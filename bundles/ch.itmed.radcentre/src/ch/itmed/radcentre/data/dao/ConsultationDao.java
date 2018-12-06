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

import java.util.ArrayList;
import java.util.List;

public final class ConsultationDao {
	private String serviceProviderGln;
	private String date;
	private String sessionId;
	private String visitNumber;
	private List<String> tarmedCodes = new ArrayList<>();
	private List<Integer> tarmedQuantities = new ArrayList<>();
	private List<TarmedSide> tarmedSides = new ArrayList<>();
	private List<String> articlesGtins = new ArrayList<>();
	private List<Integer> articleQuantities = new ArrayList<>();
	private List<String> diagnoses = new ArrayList<>();

	public String getServiceProviderGln() {
		return serviceProviderGln;
	}

	public String getDate() {
		return date;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public String getVisitNumber() {
		return visitNumber;
	}

	public List<String> getTarmedCodes() {
		return tarmedCodes;
	}

	public List<Integer> getTarmedQuantities() {
		return tarmedQuantities;
	}

	public List<TarmedSide> getTarmedSides() {
		return tarmedSides;
	}
	
	public List<String> getArticleGtins () {
		return articlesGtins;
	}
	
	public List<Integer> getArticleQuantities () {
		return articleQuantities;
	}
	
	public List<String> getDiagnoses() {
		return diagnoses;
	}

	public void setServiceProviderGln(String serviceProviderGln) {
		this.serviceProviderGln = serviceProviderGln;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public void setVisitNumber(String visitNumber) {
		this.visitNumber = visitNumber;
	}

	public void addTarmedCode(String tarmedCode) {
		tarmedCodes.add(tarmedCode);
	}

	public void addTarmedQuantity(int tarmedQuantity) {
		tarmedQuantities.add(tarmedQuantity);
	}
	
	public void addArticleGtin(String gtin) {
		articlesGtins.add(gtin);
	}
	
	public void addArticleQuantity(int quantity) {
		articleQuantities.add(quantity);
	}

	public void addTarmedSide(String tarmedCode, String tarmedSide) {
		tarmedSides.add(new TarmedSide(tarmedCode, tarmedSide));
	}

	public class TarmedSide {
		private String tarmedCode;
		private String tarmedSide;
		private int index;

		public TarmedSide(String tarmedCode, String tarmedSide) {
			this.tarmedCode = tarmedCode;
			this.tarmedSide = tarmedSide;
			this.index = tarmedSides.size();
		}

		public String getTarmedCode() {
			return tarmedCode;
		}

		public String getTarmedSide() {
			return tarmedSide;
		}
		
		public int getIndex() {
			return index;
		}

		public void setTarmedCode(String tarmedCode) {
			this.tarmedCode = tarmedCode;
		}

		public void setTarmedSide(String tarmedSide) {
			this.tarmedSide = tarmedSide;
		}
	}
	
	public void addDiagnosis(String tiCode) {
		diagnoses.add(tiCode);
	}

}
