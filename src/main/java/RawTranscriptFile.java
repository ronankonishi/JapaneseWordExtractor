import javax.sound.sampled.Line;
import java.io.*;
import java.nio.Buffer;
import java.text.BreakIterator;

public class RawTranscriptFile {

    private String filePath;

    public RawTranscriptFile(String filePath) {
        this.filePath = filePath;
    }

    public void exportWordLines(String targetFilePath) {
        BufferedReader reader;
        LineCounter lc = new LineCounter(2);

        try {
            File targetFile = new File(targetFilePath);
            targetFile.createNewFile();
            FileWriter fileWriter = new FileWriter(targetFile);

            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();

            boolean shouldAddNewLine = false;
            boolean isFirstLine = true;
            while (line != null) {
                if (lc.isMax()) {
                    if (line.equals("")){
                        lc.reset();
                    } else {
                        if (isFirstLine) {
                            if (shouldAddNewLine) {
                                fileWriter.write("\n" + line);
                            } else {
                                shouldAddNewLine = true;
                                fileWriter.write(line);
                            }
                        } else {
                            fileWriter.write(line);
                        }
                        isFirstLine = false;
                    }
                } else {
                    lc.inc();
                    isFirstLine = true;
                }

                line = reader.readLine();
            }

            fileWriter.close();
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class LineCounter {
        private int count;
        private int max;

        public LineCounter(int max) {
            this.count = 0;
            this.max = max;
        }

        public LineCounter(int max, int initCount) {
            this.count = initCount;
            this.max = max;
        }

        public void inc() {
            count++;
        }

        public boolean isMax() {
            return count == max;
        }

        public void reset() {
            count = 0;
        }
    }


}
