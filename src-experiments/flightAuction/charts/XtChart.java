package flightAuction.charts;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.apache.commons.lang3.ArrayUtils;
import org.math.plot.Plot2DPanel;


public class XtChart {

	public static void main(String[] args) {
		Plot2DPanel plot = new Plot2DPanel();
		int gameLength = 540000;
		
		for(int UB = -10; UB<=30; UB++){
			ArrayList<Double> times = new ArrayList<Double>();
			ArrayList<Double> xtA = new ArrayList<Double>();
			
			for(long timeInGame = 0; timeInGame<gameLength; timeInGame=timeInGame+10000){
				double xt = 10 + (((double) timeInGame / gameLength) * (UB - 10));
				times.add(new Double(timeInGame/1000));
				xtA.add(xt);
			}
			double[] x = ArrayUtils.toPrimitive(times.toArray(new Double[times.size()]));
			double[] y = ArrayUtils.toPrimitive(xtA.toArray(new Double[xtA.size()]));
			plot.addLinePlot("my plot", x, y);
		}
		JFrame frame = new JFrame("a plot panel");
		frame.setSize(new Dimension(500, 500));
		frame.setContentPane(plot);
		frame.setVisible(true);
	}

}
