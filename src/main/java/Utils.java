import java.util.*;

public class Utils {
    public static HashMap<String, Integer> getCountTable(List<String> words) {
        HashMap<String, Integer> countTable = new HashMap<String, Integer>();

        for (String word : words) {
            if (countTable.containsKey(word)) {
                countTable.replace(word, countTable.get(word) + 1);
            } else {
                countTable.put(word, 1);
            }
        }

        return countTable;
    }

    public static List<String> getCommonWords(Map<String, Integer> countTable, int threshold) {
        List<String> commonWords = new ArrayList<String>();

        Iterator<Map.Entry<String, Integer>> iter = countTable.entrySet().iterator();
        while(iter.hasNext()) {
            Map.Entry<String, Integer> entry = iter.next();
            if (entry.getValue() >= threshold) {
                commonWords.add(entry.getKey());
            }
        }

        return commonWords;
    }
}
