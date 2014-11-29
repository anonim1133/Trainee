package me.anonim1133.trainee.utils;

import android.content.Context;
import android.text.format.Time;

import java.io.FileOutputStream;
import java.io.IOException;

public class GpxBuilder {
	private static final String CREATOR = "Trainee";

	private Context c;
	private FileOutputStream outputStream;

	private String filename;

	public GpxBuilder(Context context, String activity_type, String author){
		this.c = context;

		Time today = new Time();
		today.setToNow();

		String time = today.format2445();

		filename = time + ".gpx";

		try {
			outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<gpx version=\"1.1\" creator=\"" + CREATOR + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
				"xmlns=\"http://www.topografix.com/GPX/1/0\" \n" +
				"xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n" +
				"   <metadata>\n" +
				"       <author>\n" +
				"           <name>" + author + "</name>\n" +
				"       </author>\n" +
				"       <time>" + time + "</time>\n" +
				"   </metadata>\n" +
				"   <trk>\n" +
				"       <type>" + activity_type + "</type>\n" +
				"       <trkseg>");
	}

	private void write(String string){
		try {
			outputStream.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addPoint(double lat, double lon, double ele, float speed, long time){

		Time today = new Time();
		today.set(time);
		String time2445 = today.format2445();

		String string = "<trkpt lat=\"" + String.valueOf(lat) + "\" lon=\"" + String.valueOf(lon) + "\">\n" +
				"\t\t<ele>" + String.valueOf(ele) + "</ele>\n" +
				"\t\t<time>" + time2445 + "</time>\n" +
				"\t\t<speed>" + String.valueOf(speed) + "</speed>\n" +
				"\t\t</trkpt>\n";

		try {
			outputStream.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addPoint(double lat, double lon, double ele, float speed, int steps, long time){
		String string = "<trkpt lat=\"" + String.valueOf(lat) + "\" lon=\"" + String.valueOf(lon) + "\">\n" +
				"\t\t<ele>" + String.valueOf(ele) + "</ele>\n" +
				"\t\t<time>" + String.valueOf(time) + "</time>\n" +
				"\t\t<speed>" + String.valueOf(speed) + "</speed>\n" +
				"\t\t<steps>" + String.valueOf(speed) + "</steps>\n" +
				"\t\t</trkpt>\n";

		try {
			outputStream.write(string.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String close() {
		write("     </trkseg>\n" +
				"   </trk>\n" +
				"</gpx>");

		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return filename;
	}
}
