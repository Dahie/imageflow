/**
 * 
 */
package models;

import junit.framework.TestCase;


/**
 * @author danielsenff
 *
 */
public class ParameterFactoryTests extends TestCase {

	/**
	 * 
	 */
	public void testStringParameter() {
		String paramValue = "Test String";
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof StringParameter);
		assertTrue("is right class", classInstance);
	}
	
	/**
	 * 
	 */
	public void testBooleanParameter() {
		boolean paramValue = true;
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof BooleanParameter);
		assertTrue("is right class", classInstance);
	}
	
	/**
	 * 
	 */
	public void testDoubleParameter() {
		double paramValue = 4.3;
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof DoubleParameter);
		assertTrue("is right class", classInstance);
	}
	
	/**
	 * 
	 */
	public void testIntegerParameter() {
		double paramValue = 20;
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof DoubleParameter);
		assertTrue("is right class", classInstance);
	}
	
	private void assertParameter(String text, Parameter expectedParameter, Parameter resultParameter) {
		
	}
	
}
