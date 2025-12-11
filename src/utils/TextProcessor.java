package utils;

import java.util.*;
import java.util.regex.Pattern;

public class TextProcessor {
    private static final Set<String> stopwords = Set.of(
        "a", "an", "the", "and", "or", "but", "if", "is", "are", "was", "were", "this", "that"
    );

    private static final Pattern WORD_PATTERN = Pattern.compile("\\W+");

    public static List<String> preprocess(String text) {
        String[] words = WORD_PATTERN.split(text.toLowerCase());
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (!stopwords.contains(word) && !word.isBlank()) {
                result.add(word);
            }
        }
        return result;
    }
}
