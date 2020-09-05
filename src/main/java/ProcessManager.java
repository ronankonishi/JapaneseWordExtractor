import javax.swing.*;
import java.util.HashMap;
import java.util.List;

public class ProcessManager {

    private String websiteURL;
    private String videoTitle;
    private String transcriptDownloadLink;
    private String downloadPath;
    private String chromeDriverPath;
    private String zipFilePath;
    private String japaneseExportFilePath;

    public ProcessManager() {
        websiteURL = "https://itazuraneko.neocities.org/library/sub.html";
        videoTitle = JOptionPane.showInputDialog("Video Title: ");
        downloadPath = "C:\\Users\\Ronan\\Downloads\\test";
        chromeDriverPath = "C:\\Users\\Ronan\\Downloads\\chromedriver_win32\\chromedriver.exe";
        japaneseExportFilePath = "C:\\Users\\Ronan\\Downloads\\test\\export.txt";
    }

    public void run() {
        transcriptDownloadLink = WebScraper.getTranscriptDownloadLink(websiteURL, videoTitle);
        System.out.println("Retrieved Transcript Download Link: " + transcriptDownloadLink);

        WebScraper.downloadTranscriptZipFile(chromeDriverPath, downloadPath, transcriptDownloadLink);

        zipFilePath = downloadPath + "\\" + videoTitle + ".zip";
        ZipFile zipFile = new ZipFile(zipFilePath);

        String newFileURL = zipFile.unzip();
        zipFile.delete();
        
        RawTranscriptFile rtf = new RawTranscriptFile(newFileURL);
        rtf.exportWordLines(japaneseExportFilePath);

        JapaneseTextFile jtf = new JapaneseTextFile(japaneseExportFilePath);
        List allWords = jtf.getWordsFromFilePath();

        HashMap<String, Integer> countTable = Utils.getCountTable(allWords);

        System.out.println(Utils.getCommonWords(countTable, 5));
    }
}