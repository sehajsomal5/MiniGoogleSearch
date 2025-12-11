package cli;

import indexer.IndexBuilder;
import search.SearchEngine;
import search.SearchEngine.SearchResult;

import java.io.IOException;
import java.util.*;
import java.io.ObjectInputStream;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length > 0 && args[0].equals("--index")) {
                IndexBuilder builder = new IndexBuilder();
                builder.indexDirectory("data");
                builder.saveIndex("index.ser");
                System.out.println("Indexing complete.");
            } else if (args.length > 1 && args[0].equals("--query")) {
                StringBuilder queryBuilder = new StringBuilder();
                int topK = 5;

                for (int i = 1; i < args.length; i++) {
                    if (args[i].equals("--top") && i + 2 < args.length && args[i + 1].equals("k")) {
                        topK = Integer.parseInt(args[i + 2]);
                        i += 2;
                    } else {
                        queryBuilder.append(args[i]).append(" ");
                    }
                }

                String query = queryBuilder.toString().trim();
                Map<String, Map<String, Integer>> index = loadIndex("index.ser");
                Map<String, Integer> docLengths = IndexBuilder.loadDocLengths("doclengths.ser");
                Map<String, Integer> docFrequencies = IndexBuilder.loadDocFrequencies("docfreqs.ser");
                int totalDocs = IndexBuilder.loadTotalDocs("totaldocs.ser");

                SearchEngine engine = new SearchEngine(index, docLengths, docFrequencies, totalDocs);
                List<SearchResult> results = engine.search(query, topK);

                System.out.printf("? Top results for query: \"%s\"%n", query);
                for (SearchResult r : results) {
                    System.out.printf("? %s (score: %.3f)%n", r.filename(), r.score());
                }
            } else {
                System.out.println("Usage:\n  java -cp bin cli.Main --index\n  java -cp bin cli.Main --query <text> --top k <number>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Integer>> loadIndex(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new java.io.FileInputStream(path))) {
            return (Map<String, Map<String, Integer>>) in.readObject();
        }
    }
}
