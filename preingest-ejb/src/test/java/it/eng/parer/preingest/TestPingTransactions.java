/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.preingest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.test.tx.TestTransactions;
import it.eng.parer.test.tx.TestVerifier;
import it.eng.parer.test.tx.dto.ConfigurationDto;
import it.eng.parer.test.tx.dto.OutcomeDto;
import it.eng.parer.test.tx.dto.Step;
import it.eng.parer.test.tx.dto.step.DBStep;
import it.eng.parer.test.tx.dto.step.ExceptionStep;
import it.eng.parer.test.tx.dto.step.MainOperationStep;
import it.eng.parer.test.tx.dto.step.XadiskStep;
import it.eng.parer.test.tx.exceptions.EccezioneAttesa;

/**
 * Test delle transazioni su jboss. I test descritti qui permettono di effettuare delle transazioni
 * 2pc tra db, xadisk e le code JMS.
 *
 * @author Snidero_L
 */
class TestPingTransactions {

    private static final Logger LOG = LoggerFactory.getLogger(TestPingTransactions.class);

    private static TestTransactions pingTestTransactions;
    private static TestVerifier pingTestVerifier;

    @BeforeAll
    static void setUpClass() {
	try {
	    final Properties jndiProperties = new Properties();
	    jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
	    final Context context = new InitialContext(jndiProperties);

	    pingTestTransactions = (TestTransactions) context
		    .lookup("ejb:/parer-test-suite/TestTransactionsBeanBMT!"
			    + TestTransactions.class.getName());
	    pingTestVerifier = (TestVerifier) context.lookup(
		    "ejb:/parer-test-suite/TestVerifierBean!" + TestVerifier.class.getName());
	} catch (Exception e) {
	    LOG.trace("Impossibile effettuale la lookup", e);
	}
    }

    @AfterAll
    static void tearDownClass() {
	// non implementato
    }

    @BeforeEach
    void setUp() {
	boolean isAlive = false;
	try {
	    isAlive = (pingTestTransactions != null && pingTestTransactions.isAlive());
	} catch (Exception e) {
	    // Il server non è su. Ingoio l'eccezione
	    LOG.trace("Impossibile contattare l'ejb remoto", e);
	}

	assumeTrue(isAlive);
    }

    @AfterEach
    void tearDown() {
	// non implementato
    }

    @Test
    void testSimple() throws EccezioneAttesa {
	ConfigurationDto config = new ConfigurationDto();
	String message = "Test semplice senza fallimento";
	DBStep dbStep = new DBStep(message);
	XadiskStep xaStep = new XadiskStep(message, "xaDiskSimple.txt");

	config.addStep(dbStep);
	config.addStep(xaStep);

	pingTestTransactions.testSimple(config);

	OutcomeDto verifyTest = pingTestVerifier.verifyTest(config);
	assertThat(verifyTest.get(dbStep), is(Boolean.TRUE));
	assertThat(verifyTest.get(xaStep), is(Boolean.TRUE));

    }

    @Test
    void testQueueProducer() throws EccezioneAttesa {
	ConfigurationDto config = new ConfigurationDto();

	String message = "Test queue producer senza fallimento";

	DBStep dbStep = new DBStep(message);
	XadiskStep xaStep = new XadiskStep(message, "xaDiskQProducer.txt");
	MainOperationStep mainStep = new MainOperationStep(message,
		MainOperationStep.OperationType.PRODUCE);

	config.addSteps(new Step[] {
		dbStep, xaStep, mainStep });

	pingTestTransactions.testQueueProducer(config);

	OutcomeDto verifyTest = pingTestVerifier.verifyTest(config);
	assertThat(verifyTest.get(dbStep), is(Boolean.TRUE));
	assertThat(verifyTest.get(xaStep), is(Boolean.TRUE));

    }

    @Test
    void testQueueConsumer() {
	ConfigurationDto config = new ConfigurationDto();

	String message = "Test queue consumer senza fallimento";

	DBStep dbStep = new DBStep(message);
	XadiskStep xaStep = new XadiskStep(message, "xaDiskQConsumer.txt");
	MainOperationStep mainStep = new MainOperationStep(message,
		MainOperationStep.OperationType.CONSUME);

	config.addSteps(new Step[] {
		xaStep, mainStep, dbStep });

	pingTestTransactions.testQueueConsumer(config);

	OutcomeDto verifyTest = pingTestVerifier.verifyTest(config);
	assertThat(verifyTest.get(dbStep), is(Boolean.TRUE));
	assertThat(verifyTest.get(xaStep), is(Boolean.TRUE));
    }

    @Test
    void testSimpleWithException() {
	ConfigurationDto config = new ConfigurationDto();
	String message = "Test simple con fallimento al passo 2";

	DBStep dbStep = new DBStep(message);
	XadiskStep xaStep = new XadiskStep(message, "xaDiskException.txt");
	ExceptionStep excStep = new ExceptionStep(message);

	config.addSteps(new Step[] {
		xaStep, excStep, dbStep });

	assertThrows(EccezioneAttesa.class, () -> {
	    pingTestTransactions.testSimple(config);
	});
    }

    @Test
    void testSimpleWithExceptionAndOutcome() {
	ConfigurationDto config = new ConfigurationDto();

	String message = "Test simple con fallimento al passo 2";

	DBStep dbStep = new DBStep(message);
	XadiskStep xaStep = new XadiskStep(message, "xaDiskException.txt");
	ExceptionStep excStep = new ExceptionStep(message);

	config.addSteps(new Step[] {
		xaStep, excStep, dbStep });
	try {
	    pingTestTransactions.testSimple(config);
	} catch (EccezioneAttesa e) {
	    // Ignoro l'eccezione
	    LOG.debug("Eccezione attesa", e);
	}
	OutcomeDto verifyTest = pingTestVerifier.verifyTest(config);
	assertThat(verifyTest.get(dbStep), is(Boolean.TRUE));
	assertThat(verifyTest.get(xaStep), is(Boolean.TRUE));

    }

    @Test
    void testSimpleMultipleXadisk() throws EccezioneAttesa {
	ConfigurationDto config = new ConfigurationDto();
	DBStep dbStep = new DBStep("Test semplice con 2 step Xadisk");
	XadiskStep xaStep1 = new XadiskStep("Test Xadisk 1 di 2", "xaDiskMultpleSimple1-2.txt");
	XadiskStep xaStep2 = new XadiskStep("Test Xadisk 2 di 2", "xaDiskMultpleSimple2-2.txt");

	config.addStep(xaStep1);
	config.addStep(dbStep);
	config.addStep(xaStep2);

	pingTestTransactions.testSimple(config);

	OutcomeDto verifyTest = pingTestVerifier.verifyTest(config);
	assertThat(verifyTest.get(dbStep), is(Boolean.TRUE));
	assertThat(verifyTest.get(xaStep1), is(Boolean.TRUE));
	assertThat(verifyTest.get(xaStep2), is(Boolean.TRUE));

    }

}
