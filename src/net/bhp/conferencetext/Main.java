package net.bhp.conferencetext;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Main {

	private static final String URL_PREFIX = "https://www.lds.org/general-conference/sessions/";
	private static final String URL_POSTFIX = "?lang=eng";
	private static final String SAVE_LOCATION = "C:/Users/randallbooth/Downloads/GC Text/";
	private static List<Exception> errorLog = new ArrayList<>();

	public static void main(String[] args) {
		Main.makeItHappen();
	}

	public static void makeItHappen() {
		long startTime = System.currentTimeMillis();
		List<SaveResult> saveResults = new ArrayList<>();
		List<URL> talkUrls = new ArrayList<>();
		System.out.print("Retrieving conference URLs...");
		List<URL> conferenceUrls = getAllConferenceUrls();
		System.out.println(conferenceUrls.size());
		System.out.print("Retrieving talk URLs...");
		conferenceUrls.parallelStream().forEach(c -> {
			talkUrls.addAll(getTalkUrls(c));
		});
		System.out.println(talkUrls.size());
		System.out.print("Saving talks to disk...");
		talkUrls.parallelStream().forEach(t -> {
			saveResults.add(saveTalk(t));
		});
		System.out.println(saveResults.stream().filter(sr -> sr.isSuccess()).count() + " saved, " + saveResults.stream().filter(sr -> !sr.isSuccess()).count()
		                + " errors.");
		saveResults.stream().forEach(sr -> {
			if (sr.isSuccess()) {
				System.out.println("Saved " + sr.getBytes() + " bytes to " + sr.getPath());
			} else {
				System.out.println("Error while saving to " + sr.getPath() + ": " + sr.getMessage());
			}
		});
		System.out.println();
		long millis = (System.currentTimeMillis() - startTime);
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
					    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
					    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
		System.out.println("Time to complete: " + hms);
		System.out.println();
		System.out.println("Error Log:");
		errorLog.stream().forEach(err -> {
			System.out.println("\t" + err.getMessage());
		});
	}

	private static List<URL> getAllConferenceUrls() {
		List<URL> urls = new ArrayList<>();
		ConferenceDateHelper.generateAllConferenceDates().parallelStream().forEach(cd -> {
			try {
				urls.add(URI.create(URL_PREFIX + cd.toDateString() + URL_POSTFIX).toURL());
			} catch (Exception e) {
				errorLog.add(e);
			}
		});

		return urls;
	}

	private static List<URL> getTalkUrls(URL url) {
		List<URL> urlsToConferenceTalks = new ArrayList<>();
		try {
			urlsToConferenceTalks.addAll(internalRetrieveTalkUrls(url));
		} catch (Exception e) {
			errorLog.add(e);
		}
		return urlsToConferenceTalks;
	}

	private static List<URL> internalRetrieveTalkUrls(URL url) {
		List<URL> talkUrls = new ArrayList<>();
		Document doc = createDocument(url);
		Elements links = doc.select("span.talk a[href]");
		links.parallelStream().forEach((el) -> {
			try {
				talkUrls.add(URI.create(el.attr("href")).toURL());
			} catch (Exception e) {
				errorLog.add(e);
			}
		});
		return talkUrls;
	}
	
	private static Document createDocument(URL url) {
		Document doc = null;
		try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuilder html = new StringBuilder();
	        while (reader.ready()) {
	        	html.append(reader.readLine());
	        }
	        doc = Jsoup.parse(html.toString());
        } catch (IOException e) {
	        errorLog.add(e);
        }
		return doc;
	}

	private static SaveResult saveTalk(URL talkUrl) {
		SaveResult saveResult = new SaveResult();
		String filePath = null;
		if (talkUrl != null) {
			try {
				filePath = SAVE_LOCATION + getTalkName(talkUrl) + ".html";
				String textFilePath = SAVE_LOCATION + "/text/" + getTalkName(talkUrl) + ".txt";
				saveResult.setPath(filePath);
				Document doc = createDocument(talkUrl);
				Files.copy(new ByteArrayInputStream(doc.text().getBytes(StandardCharsets.UTF_8)), FileSystems.getDefault().getPath(textFilePath));
				URLConnection con = talkUrl.openConnection();
				con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				con.addRequestProperty("User-Agent",
				                "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");

				saveResult.setBytes(Files.copy(con.getInputStream(), FileSystems.getDefault().getPath(filePath)));
				saveResult.setSuccess(saveResult.getBytes() > 0);
			} catch (Exception e) {
				saveResult.setMessage(e.getMessage());
				saveResult.setSuccess(false);
				errorLog.add(e);
			}
		}

		return saveResult;
	}

	private static String getTalkName(URL url) {
		String urlString = url.toExternalForm();
		String[] urlPieces = urlString.split("\\/");
		StringBuilder result = new StringBuilder();
		result.append(urlPieces[urlPieces.length - 3] + "-");
		result.append(urlPieces[urlPieces.length - 2] + "-");
		result.append(urlPieces[urlPieces.length - 1].split("\\?")[0]);
		return result.toString();
	}
}
