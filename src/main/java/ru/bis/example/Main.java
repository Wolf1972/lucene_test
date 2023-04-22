package ru.bis.example;

import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import ru.bis.example.index.MessageIndexer;
import ru.bis.example.index.MessageToDocument;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    final static private int BATCH_SIZE = 1000;
    final static private String PATH_TO_INDEX = "/tmp/teaser_index";

    public static void main(String[] args) throws IOException, ParseException {

        System.out.println("Lucene example.");

        final MessageIndexer indexer = new MessageIndexer(PATH_TO_INDEX);
        if (Files.exists(Paths.get(PATH_TO_INDEX))) {
            try {
                IndexReader indexReader = indexer.readIndex();
                System.out.println("Index num docs: " + indexReader.numDocs());
            }
            catch (IndexNotFoundException e) {
                System.out.println("Index directory is empty.");
            }
        }

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Choose operation with index:");
        System.out.println("0 - none (by default)");
        System.out.println("1 - append");
        System.out.println("2 - create");
        System.out.print("?: ");
        String operationStr = console.readLine();

        long startTime = 0;

        if (operationStr.equals("1") || operationStr.equals("2")) {
            String opStart;
            String opEnd;
            if (operationStr.equals("1")) {
                opStart = "append";
                opEnd = "appended";
            }
            else {
                opStart = "create";
                opEnd = "created";
            }
            System.out.println("Operation with index: " + opStart);
            System.out.print("Source file: ");
            String sourceFile = console.readLine();
            FileReader file = new FileReader(sourceFile);
            BufferedReader reader = new BufferedReader(file);
            String line = null;
            startTime = System. currentTimeMillis();
            int lineCount = 0;
            int batchCount = 0;
            boolean firstBatch = true;
            ArrayList<Document> list = new ArrayList<>();
            do {
                line = reader.readLine();
                if (line == null || batchCount >= BATCH_SIZE) {
                    indexer.index(firstBatch && (operationStr.equals("2")), list); // first iteration index created (if mode == create), then updated only
                    firstBatch = false;
                    list.clear();
                    batchCount = 0;
                    System.out.println(lineCount + " lines indexed in " + ((System.currentTimeMillis() - startTime) / 1000) + " s.");
                    if (line == null) break;
                }
                list.add(MessageToDocument.createWith(Integer.toString(lineCount), line, "4081781010000" + String.format("%07d", lineCount)));
                lineCount++; batchCount++;
            }
            while (true);
            System.out.println("Index " + opEnd + " for " + lineCount + " string(s) in " + ((System.currentTimeMillis() - startTime) / 1000) + " s.");
        }

        final SearchVariants search = new SearchVariants(indexer.readIndex());
        String toSearch = null;
        do {
            System.out.print("Query (empty string for exit):\t");
            toSearch = console.readLine();
            if (toSearch == null || toSearch.isEmpty()) break;
            startTime = System.currentTimeMillis();
//            search.fuzzySearch(toSearch); // Only fuzzy search
            search.searchWithQuery(toSearch);  // Search with query
            System.out.println("Query execution time: " + (System. currentTimeMillis() - startTime) + " ms.");
        }
        while (true);

    }
}
