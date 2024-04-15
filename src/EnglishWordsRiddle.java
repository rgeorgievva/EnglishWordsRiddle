import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EnglishWordsRiddle {

    private static final Set<String> DICTIONARY;

    static {
        try {
            DICTIONARY = loadAllWords();
        } catch (IOException e) {
            System.out.println("Unable to load dictionary due to " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        List<String> matches = solveRiddle();
        long endTime = System.nanoTime();

        long executionTime = (endTime - startTime) / 1000000;

        System.out.println("Solving the riddle took " + executionTime + "ms");
        System.out.println("Matching words count: " + matches.size());

        for (String match : matches) {
            System.out.println(match);
        }
    }

    private static Set<String> loadAllWords() throws IOException {
        URL url = new URL("https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt");

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
            return bufferedReader.lines().skip(2).collect(Collectors.toSet());
        }

    }

    /**
     * Method which finds all 9 letter english words meeting the following criteria:
     * <li> It's possible to remove one letter from the word and get a valid 8 letter word </li>
     * <li> One letter can be removed from the 8-letter word and a valid 7-letter word can be obtained</li>
     * <li> From the 7-letter word one letter can be removed and a valid 6-letter word is obtained and etc, until a
     * one-letter valid word is obtained</li>
     * <li> Valid single letter words are "I" and "A"</li>
     * 
     * @return List of all words meeting the above criteria
     */
    private static List<String> solveRiddle() {
        List<String> matches = new ArrayList<>();
        for (String word : DICTIONARY) {
            if (word.length() != 9 || (!word.contains("I") && !word.contains("A"))) {
                continue;
            }
            String output = checkWordValidity(word);
            if (Objects.nonNull(output)) {
                matches.add(word);
            }
        }

        return matches;
    }


    private static String checkWordValidity(String word) {
        if (word.length() == 1) {
            return word.equals("A") || word.equals("I") ? word : null;
        }

        for (int i = 0; i < word.length(); i++) {
            String wordWithoutLetter = word.substring(0, i) + word.substring(i + 1);

            if ((DICTIONARY.contains(wordWithoutLetter)
                    || wordWithoutLetter.equals("I")
                    || wordWithoutLetter.equals("A"))) {
                wordWithoutLetter = checkWordValidity(wordWithoutLetter);
                if (Objects.nonNull(wordWithoutLetter)) {
                    return wordWithoutLetter;
                }
            }
        }

        return null;
    }
}
