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

package ch.itmed.radcentre.importer;

import java.util.List;
import java.util.function.Consumer;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.data.TICode;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Artikel;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Query;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.itmed.radcentre.data.dao.ConsultationDao;
import ch.itmed.radcentre.data.dao.ConsultationDao.TarmedSide;
import ch.itmed.radcentre.ui.MessageBoxUtil;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConsultationImporter {
	private Fall fall;
	private ConsultationDao consultationDao;
	private Konsultation konsultation;
	private int tarmedIndex;
	private int articleIndex;
	private boolean serviceResult;
	private Result<IVerrechenbar> failedService;

	private static Logger logger = LoggerFactory.getLogger(ConsultationImporter.class);

	public ConsultationImporter(Fall fall, ConsultationDao consultationDao) {
		this.consultationDao = consultationDao;
		this.fall = fall;
		this.tarmedIndex = 0;
		this.articleIndex = 0;
		this.serviceResult = true;
	}

	public void addConsultation() {
		if (!checkExistingConsultation()) {
			addNewConsultation();
		}
	}

	/**
	 * Checks for an existing Konsultation for a specific date
	 * 
	 * @return
	 */
	private boolean checkExistingConsultation() {
		String consId = fall
				.getInfoString("RadCentreSession" + consultationDao.getVisitNumber() + consultationDao.getSessionId());
		if (consId.isEmpty()) {
			return false;
		}

		Konsultation cons = Konsultation.load(consId);
		if (cons != null) {
			if (cons.get("deleted").equals("1")) {
				return false;
			}
			konsultation = cons;
			updateExistingConsultation();
			return true;
		}
		return false;
	}

	private void addNewConsultation() {
		konsultation = fall.neueKonsultation();
		fall.setInfoString("RadCentreSession" + consultationDao.getVisitNumber() + consultationDao.getSessionId(),
				konsultation.getId());
		konsultation.setDatum(consultationDao.getDate(), false);
		if (addServiceProvider()) {
			addDiagnose();
			addServices();
		}
	}

	private void updateExistingConsultation() {
		// First we remove all services
		List<Verrechnet> services = konsultation.getLeistungen();
		services.stream().forEach(s -> konsultation.removeLeistung(s));
		// Second we persist the new data
		konsultation.setDatum(consultationDao.getDate(), false);
		if (addServiceProvider()) {
			addDiagnose();
			addServices();
		}
	}

	private boolean addServiceProvider() {
		try {
			konsultation.setMandant(KontaktImporter.getServiceProviderFromGln(consultationDao.getServiceProviderGln()));
			return true; // ServiceProvider has been added
		} catch (Exception e) {
			logger.error("Failed to add service provider", e);
			failedService = new Result<IVerrechenbar>(Result.SEVERITY.WARNING, 2,
					"Failed to add service provider with GLN: " + consultationDao.getServiceProviderGln(), null, false);

			deleteConsultation();
			return false; // ServiceProvider could not be added
		}
	}

	// If an error occurs, delete the Konsultation
	private void deleteConsultation() {
		logger.error(failedService.toString());
		konsultation.delete(true);

		throw new RuntimeException("Failed to create consultation. " + failedService);
	}

	// Add all IVerrechenbar to Konsultation
	private void addServices() {
		ServiceConsumer consumer = new ServiceConsumer();
		consultationDao.getTarmedCodes().stream().forEach(consumer);
		addTarmedSides();
		addArticles();

		if (!serviceResult) {
			MessageBoxUtil.setErrorMsg(failedService.toString());
			deleteConsultation();
		}
	}

	private class ServiceConsumer implements Consumer<String> {
		@Override
		public void accept(String c) {
			int tq = consultationDao.getTarmedQuantities().get(tarmedIndex);
			for (int i = 0; i < tq; i++) {
				IVerrechenbar tl = TarmedLeistung.getFromCode(c, new TimeTool(consultationDao.getDate()),
						fall.getConfiguredBillingSystemLaw().getLocaleText());

				if (tl == null) {
					tarmedIndex++;
					serviceResult = false;
					failedService = new Result<IVerrechenbar>(Result.SEVERITY.WARNING, 2,
							"No TarmedLeistung found with code: " + c, null, false);
					MessageBoxUtil.setErrorMsg("Ungültiger Tarmed Code \"" + c + " \"");
					return;
				}

				Result<IVerrechenbar> result = konsultation.addLeistung(tl);

				if (!result.isOK()) {
					boolean ignoreErr = false;

					List<Verrechnet> tarmed = konsultation.getLeistungen();
					System.out.println(tarmed.size());
					for (Verrechnet leistung : konsultation.getLeistungen()) {
						// Test if the code is already included in the consultation
						// If this is the case, ignore the error and continue
						if (leistung.getCode().equals(c)) {
							ignoreErr = true;
						}
					}

					if (!ignoreErr) {
						serviceResult = false;
						failedService = result;
					}
				}
			}
			tarmedIndex++;
		}
	}

	private void addTarmedSides() {
		List<TarmedSide> sides = consultationDao.getTarmedSides();
		List<TarmedSide> mask = consultationDao.getTarmedSides(); // is used as a mask to filter objects from List sides

		for (Verrechnet leistung : konsultation.getLeistungen()) {
			TarmedSide side = sides.stream().filter(s -> s.getTarmedCode().equals(leistung.getCode())).findFirst()
					.orElse(null);

			if (side != null) {
				sides.removeIf(s -> mask.contains(side));
				leistung.setDetail(TarmedLeistung.SIDE, side.getTarmedSide());
			}
		}
	}

	private void addArticles() {
		ArticleConsumer consumer = new ArticleConsumer();
		consultationDao.getArticleGtins().forEach(consumer);
	}

	private class ArticleConsumer implements Consumer<String> {
		@Override
		public void accept(String gtin) {
			Artikel article = getArtikelFromGtin(gtin);

			if (article == null) {
				articleIndex++;
				serviceResult = false;
				failedService = new Result<IVerrechenbar>(Result.SEVERITY.WARNING, 2,
						"No Artikel found with GTIN: " + gtin, null, false);
				return;
			}

			int aq = consultationDao.getArticleQuantities().get(articleIndex);

			for (int i = 0; i < aq; i++) {
				Result<IVerrechenbar> result = konsultation.addLeistung(article);

				if (!result.isOK()) {
					serviceResult = false;
					failedService = result;
				}
			}
			articleIndex++;
		}
	}

	private Artikel getArtikelFromGtin(String gtin) {
		Query<Artikel> query = new Query<>(Artikel.class);
		query.add(Artikel.FLD_EAN, Query.EQUALS, gtin);
		List<Artikel> result = query.execute();

		if (result.isEmpty()) {
			logger.info("No article found with GTIN: " + gtin + " in table ARTIKEL");

			query = new Query<>(ArtikelstammItem.class);
			query.add(ArtikelstammItem.FLD_GTIN, Query.EQUALS, gtin);
			result = query.execute();

			if (result.isEmpty()) {
				logger.error("No article found with GTIN: " + gtin + " in table ARTIKELSTAMM_CH");
				MessageBoxUtil.setErrorMsg("Kein Artikel mit GTIN \"" + gtin + "\" gefunden.");
				serviceResult = false;
				return null;
			}
		}

		return result.get(0);
	}

	private void addDiagnose() {
		konsultation.getDiagnosen().forEach(d -> konsultation.removeDiagnose(d));

		if (Konsultation.getDefaultDiagnose() != null) {
			konsultation.addDiagnose(Konsultation.getDefaultDiagnose());
		}

		consultationDao.getDiagnoses().forEach(d -> konsultation.addDiagnose(TICode.getFromCode(d)));
	}

}
