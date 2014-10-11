package example;

import static org.junit.Assert.*;

import org.junit.Test;

import com.wsntools.iris.data.Constants;

public class ExampleTest {

	@Test
	public void test() {
		assertTrue(null != Constants.getResource("main/RadioMonitoringTool.class"));
	}

}
