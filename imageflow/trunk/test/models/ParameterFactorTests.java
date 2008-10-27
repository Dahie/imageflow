/**
 * 
 */
package models;

import junit.framework.TestCase;


/**
 * @author danielsenff
 *
 */
public class ParameterFactorTests extends TestCase {

	/**
	 * 
	 */
	public void testStringParameter() {
		String paramValue = "Test String";
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof StringParameter);
		assertEquals("is right class", true, classInstance);
	}
	
	/**
	 * 
	 */
	public void testBooleanParameter() {
		boolean paramValue = true;
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof BooleanParameter);
		assertEquals("is right class", true, classInstance);
	}
	
	/**
	 * 
	 */
	public void testDoubleParameter() {
		double paramValue = 4.3;
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof DoubleParameter);
		assertEquals("is right class", true, classInstance);
	}
	
	/**
	 * 
	 */
	public void testIntegerParameter() {
		double paramValue = 20;
		Parameter parameter = ParameterFactory.createParameter("testParameter", paramValue, "helpString");
		
		boolean classInstance = (parameter instanceof DoubleParameter);
		assertEquals("is right class", true, classInstance);
	}
	
	private void assertParameter(String text, Parameter expectedParameter, Parameter resultParameter) {
		
	}
	
}
