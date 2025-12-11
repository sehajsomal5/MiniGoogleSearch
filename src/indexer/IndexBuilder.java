package indexer;

import utils.TextProcessor;

import java.io.*;
import java.util.*;

public class IndexBuilder {
    private Map<String, Map<String, Integer>> index = new HashMap<>();
    private Map<String, Integer> docLengths = new HashMap<>();
    private Map<String, Integer> docFrequencies = new HashMap<>();
    private int totalDocs = 0;

    public void indexDirectory(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));

        if (files == null) return;

        for (File file : files) {
            indexFile(file);
        }
    }

    private void indexFile(File file) throws IOException {
        String docName = file.getName();
        Map<String, Integer> termFreq = new HashMap<>();
        int length = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> tokens = TextProcessor.preprocess(line);
                for (String token : tokens) {
                    termFreq.put(token, termFreq.getOrDefault(token, 0) + 1);
                    length++;
                }
            }
        }

        for (String term : termFreq.keySet()) {
            index.putIfAbsent(term, new HashMap<>());
            index.get(term).put(docName, termFreq.get(term));
            docFrequencies.put(term, docFrequencies.getOrDefault(term, 0) + 1);
        }

        docLengths.put(docName, length);
        totalDocs++;
    }

    public void saveIndex(String filePath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(index);
        }
        saveMetadata();
    }

    private void saveMetadata() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("doclengths.ser"))) {
            out.writeObject(docLengths);
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("docfreqs.ser"))) {
            out.writeObject(docFrequencies);
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("totaldocs.ser"))) {
            out.writeInt(totalDocs);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> loadDocLengths(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Map<String, Integer>) in.readObject();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Integer> loadDocFrequencies(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return (Map<String, Integer>) in.readObject();
        }
    }

    public static int loadTotalDocs(String filePath) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            return in.readInt();
        }
    }

    public Map<String, Map<String, Integer>> getIndex() {
        return index;
    }
}
