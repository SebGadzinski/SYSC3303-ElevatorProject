package project.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class miscFloorTest {

	public void realtimeWait(String chya) throws ParseException {
		String milTime = chya;
		String[] arrMilTime = milTime.split(":");
		String[] arrSec = arrMilTime[2].split("[.]");

		System.out.println("when elevator should arrive : " + arrMilTime[0] + ":" + arrMilTime[1] + ":" + arrSec[0]);

		Date dt = new Date();
		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat("kk:mm:ss");
		String currDate = dateFormat.format(dt);
		String[] currTime = currDate.split(":");
		System.out.println("current time : " + currTime[0] + ":" + currTime[1] + ":" + currTime[2] + "\n");

		int arrTime = toMilliSeconds(arrMilTime[0], arrMilTime[1], arrSec[0]);
		int currentTime = toMilliSeconds(currTime[0], currTime[1], currTime[2]);

		int timeToWait = arrTime - currentTime;
		System.out.println(timeToWait);

		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int toMilliSeconds(String hour, String min, String sec) {

		int intHour = Integer.parseInt(hour);
		int intMin = Integer.parseInt(min);
		int intSec = Integer.parseInt(sec);

		int milliH = intHour * 3600000;
		int milliM = intMin * 60000;
		int milliS = intSec * 1000;

		int total = milliH + milliM + milliS;
		return total;
	}

	public static void main(String[] args) throws ParseException {
		String penis = "15:27:17.020";
		miscFloorTest test = new miscFloorTest();
		test.realtimeWait(penis);
	}

}
