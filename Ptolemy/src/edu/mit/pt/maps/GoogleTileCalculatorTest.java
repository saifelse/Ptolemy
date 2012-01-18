package edu.mit.pt.maps;

import org.junit.Test;

public class GoogleTileCalculatorTest {

	@Test
	public void testCompute() {
		for (int z = 16; z < 22; z++) {
			double x = GoogleTileCalculator.computeGoogleX(-71092615, z);
			double y = GoogleTileCalculator.computeGoogleY(42361705, z);
			System.out.println("http://mt1.google.com/vt/x=" + (int) x + "&y="
					+ (int) y + "&z=" + z);
		}
	}

}
