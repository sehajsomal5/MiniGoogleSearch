package search;

import utils.TextProcessor;

import java.util.*;

public class SearchEngine {
    public static record SearchResult(String filename, double score) {}

    private final Map<String, Map<String, Integer>> index;
    private final Map<String, Integer> docLengths;
    private final Map<String, Integer> docFrequencies;
    private final int totalDocs;

    public SearchEngine(Map<String, Map<String, Integer>> index,
                        Map<String, Integer> docLengths,
                        Map<String, Integer> docFrequencies,
                        int totalDocs) {
        this.index = index;
        this.docLengths = docLengths;
        this.docFrequencies = docFrequencies;
        this.totalDocs = totalDocs;
    }

    public List<SearchResult> search(String query, int topK) {
        List<String> words = TextProcessor.preprocess(query);
        Map<String, Double> scores = new HashMap<>();

        for (String word : words) {
            Map<String, Integer> postings = index.get(word);
            if (postings == null) continue;

            double idf = Math.log((double) totalDocs / (1 + docFrequencies.getOrDefault(word, 1)));

            for (Map.Entry<String, Integer> entry : postings.entrySet()) {
                String doc = entry.getKey();
                int tf = entry.getValue();
                double tfidf = (1 + Math.log(tf)) * idf;

                scores.put(doc, scores.getOrDefault(doc, 0.0) + tfidf);
            }
        }

        PriorityQueue<SearchResult> pq = new PriorityQueue<>((a, b) -> Double.compare(b.score(), a.score()));
        for (Map.Entry<String, Double> e : scores.entrySet()) {
            pq.add(new SearchResult(e.getKey(), e.getValue()));
        }

        List<SearchResult> result = new ArrayList<>();
        while (!pq.isEmpty() && result.size() < topK) {
            result.add(pq.poll());
        }
        return result;
    }
}
