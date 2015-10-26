/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p/>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.applet.util;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.SignatureValue;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.applet.PinInputDialog;
import eu.europa.esig.dss.client.tsp.OnlineTSPSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;

/**
 * TODO
 *
 *
 *
 *
 *
 *
 */
public final class SigningUtils {

	private static final Logger logger = LoggerFactory.getLogger(SigningUtils.class);

	private static final OnlineTSPSource onlineTSPSource;

	//	private static AnceDataLoader dataLoader;

	private static PinInputDialog pinInputDialog;

	static {

		onlineTSPSource = new OnlineTSPSource("https://ts.certification.tn:4318");
		//		dataLoader = new AnceDataLoader();
		//		dataLoader.setContentType("application/timestamp-query");
		//		onlineTSPSource.setDataLoader(dataLoader);
	}

	private SigningUtils() {
	}

	public static DSSDocument signDocument(final File file, final XAdESSignatureParameters parameters, DSSPrivateKeyEntry privateKey,
	                                       SignatureTokenConnection tokenConnection) throws DSSException {

		try {

			final DSSDocument toSignDocument = new FileDocument(file);

			XAdESService xadesService = new XAdESService(new CommonCertificateVerifier());

			pinInputDialog = new PinInputDialog(null);
			//			dataLoader.setPinInputDialog(pinInputDialog);
			xadesService.setTspSource(onlineTSPSource);

			final ToBeSigned toBeSigned = xadesService.getDataToSign(toSignDocument, parameters);

			DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
			final SignatureValue signatureValue = tokenConnection.sign(toBeSigned, DigestAlgorithm.forName(digestAlgorithm.name()), privateKey);
			final DSSDocument dssSignature = xadesService.signDocument(toSignDocument, parameters, signatureValue);

			return dssSignature;
		} catch (Exception e) {
			throw new DSSException(e);
		}
	}
}
