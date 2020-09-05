import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WebScraper {

    public static String getTranscriptDownloadLink(String websiteURL, String videoTitle) {
        Document document = null;
        try {
            document = Jsoup.connect(websiteURL).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements videoEntries = document.select("table.sortable > tbody > tr");
        Elements videoMatches = new Elements();
        String URL = null;

        // get video entries
        for (Element videoEntry : videoEntries) {
            String name = videoEntry.select("td").get(1).text();
            if (name.equals(videoTitle)) {
                videoMatches.add(videoEntry);
            }
        }

        // get URL
        if (videoMatches.size() == 0) {
            System.out.println("No matches found");
        } else if (videoMatches.size() == 1) {
            System.out.println("Single Entry Found. Will use " + videoMatches.first().select("td").get(1).text());
            URL = videoMatches.first().select("td").get(2).select("a[href]").attr("href");
        } else {
            int index = requestVideoOption(videoMatches);
            URL = videoMatches.get(index).select("td").get(2).select("a[href]").attr("href");
        }

        return URL;
    }


    public static String downloadTranscriptZipFile(String chromeDriverPath, String downloadPath, String fileURL) {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        String downloadFilepath = downloadPath;
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        HashMap<String, Object> chromePrefs = new HashMap<String, Object>();

        options.addArguments("--headless");
        options.setExperimentalOption("prefs", chromePrefs);

        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);

        WebDriver driver = new ChromeDriver(cap);

        driver.get(fileURL);
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WebElement submitButton = driver.findElement(By.xpath("//div[contains(@class, 'download') and contains(@class, 'big-button') and contains(@class, 'download-file') and contains(@class, 'green') and contains(@class, 'transition')]"));
        submitButton.click();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Document document = Jsoup.parse(driver.getPageSource());

        System.out.println("Successfully downloaded");

        return document.body().html();
    }

    private static int requestVideoOption(Elements listOfVideos) {
        JFrame frame = new JFrame("Select a video option");
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Options: ");
        JButton button = new JButton("Submit");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        String list[] = new String[listOfVideos.size()];

        for (int i = 0; i < listOfVideos.size(); i++) {
            list[i] = listOfVideos.get(i).select("td").first().text();
        }

        JList li = new JList(list);
        li.setSelectedIndex(0);

        panel.add(label);
        panel.add(li);
        panel.add(button);

        frame.add(panel);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


        Object lock = new Object();
        Thread t = new Thread() {
            public void run() {
                synchronized(lock) {
                    while (frame.isVisible()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        t.start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent arg0) {
                synchronized (lock) {
                    frame.setVisible(false);
                    lock.notify();
                }
            }
        });

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return li.getSelectedIndex();
    }
}