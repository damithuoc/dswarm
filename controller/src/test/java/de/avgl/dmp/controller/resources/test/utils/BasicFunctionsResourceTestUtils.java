package de.avgl.dmp.controller.resources.test.utils;

import java.util.LinkedList;

import org.junit.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.avgl.dmp.persistence.model.job.Function;
import de.avgl.dmp.persistence.service.job.BasicFunctionService;

public abstract class BasicFunctionsResourceTestUtils<POJOCLASSPERSISTENCESERVICE extends BasicFunctionService<POJOCLASS>, POJOCLASS extends Function> extends
		BasicDMPResourceTestUtils<POJOCLASSPERSISTENCESERVICE, POJOCLASS> {

	public BasicFunctionsResourceTestUtils(final String resourceIdentifier, final Class<POJOCLASS> pojoClassArg,
			final Class<POJOCLASSPERSISTENCESERVICE> persistenceServiceClassArg) {

		super(resourceIdentifier, pojoClassArg, persistenceServiceClassArg);
	}

	@Override
	public void compareObjects(final POJOCLASS expectedObject, final POJOCLASS actualObject) {

		super.compareObjects(expectedObject, actualObject);

		compareFunctions(expectedObject, actualObject);
	}

	private void compareFunctions(final POJOCLASS expectedFunction, final POJOCLASS actualFunction) {

		if (expectedFunction.getFunctionDescription() != null) {

			Assert.assertNotNull("the " + pojoClassName + " description JSON shouldn't be null", actualFunction.getFunctionDescription());

			String actualFunctionDescriptionJSONString = null;

			try {

				actualFunctionDescriptionJSONString = objectMapper.writeValueAsString(actualFunction.getFunctionDescription());
			} catch (JsonProcessingException e) {

				Assert.assertTrue("something went wrong while serializing the actual " + pojoClassName + " description JSON", false);
			}

			Assert.assertNotNull("the actual " + pojoClassName + " description JSON string shouldn't be null", actualFunctionDescriptionJSONString);

			String expectedFunctionDescriptionJSONString = null;

			try {

				expectedFunctionDescriptionJSONString = objectMapper.writeValueAsString(expectedFunction.getFunctionDescription());
			} catch (JsonProcessingException e) {

				Assert.assertTrue("something went wrong while serializing the expected " + pojoClassName + " description JSON", false);
			}

			Assert.assertNotNull("the expected " + pojoClassName + " description JSON string shouldn't be null", expectedFunctionDescriptionJSONString);

			Assert.assertEquals("the " + pojoClassName + " description JSON strings are not equal", expectedFunctionDescriptionJSONString,
					actualFunctionDescriptionJSONString);
		}

		if (expectedFunction.getParameters() != null && !expectedFunction.getParameters().isEmpty()) {

			final LinkedList<String> actualFunctionParameters = actualFunction.getParameters();

			Assert.assertNotNull("the actual " + pojoClassName + " parameters shouldn't be null", actualFunctionParameters);
			Assert.assertFalse("the actual " + pojoClassName + " parameters shouldn't be empty", actualFunctionParameters.isEmpty());

			int i = 0;

			for (final String expectedFunctionParameter : expectedFunction.getParameters()) {

				final String actualFunctionParameter = actualFunctionParameters.get(i);

				Assert.assertEquals("the " + pojoClassName + " parameters are not equal", expectedFunctionParameter, actualFunctionParameter);

				i++;
			}
		}
	}
}