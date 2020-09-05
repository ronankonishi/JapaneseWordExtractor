import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JapaneseTextFile {

    private String filePath;
    private BufferedReader reader;

    public JapaneseTextFile(String filePath) {
        this.filePath = filePath;
    }

    public List getWordsFromFilePath() {
        File file = new File(filePath);
        List<String> allWords = new ArrayList<String>();

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                allWords.addAll(getWordsFromString(line));
                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return allWords;
    }

    private List getWordsFromString(String text) {
        BreakIterator iter = BreakIterator.getWordInstance(Locale.JAPANESE);
        iter.setText(text);
        List<String> words = new ArrayList<String>();

        int start = iter.first();
        int end = iter.next();
        while (end != BreakIterator.DONE) {
            words.add(text.substring(start, end));

            start = end;
            end = iter.next();
        }
        return words;
    }


}
