package edu.uci.ics.cs221.index.inverted;

import edu.uci.ics.cs221.analysis.*;
import edu.uci.ics.cs221.index.inverted.InvertedIndexManager;
import edu.uci.ics.cs221.index.inverted.InvertedIndexSegmentForTest;
import edu.uci.ics.cs221.storage.Document;
import org.junit.After;
import org.junit.Test;

import java.io.File;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class Team10MergeSegmentTest {

    private String path = "./index/team10test";

    @Test
    public void test1() {
        InvertedIndexManager invertedmanager;
        invertedmanager = InvertedIndexManager.createOrOpen(path, new ComposableAnalyzer(new PunctuationTokenizer(),new PorterStemmer()));

        invertedmanager.DEFAULT_MERGE_THRESHOLD = 4;
        Document doc0 = new Document("what is he doing today");
        Document doc1 = new Document("what a cute dog");
        Document doc2 = new Document("i saw you doing there today");
        Document doc3 = new Document("a dog is there");
        invertedmanager.addDocument(doc0);
        invertedmanager.addDocument(doc1);
        invertedmanager.flush();
        invertedmanager.addDocument(doc2);
        invertedmanager.addDocument(doc3);
        invertedmanager.flush();

        Map<String,List<Integer>> expectedlist = new HashMap<String, List<Integer>>();
        expectedlist.put("what",Arrays.asList(0,1));
        expectedlist.put("is",Arrays.asList(0,3));
        expectedlist.put("he",Arrays.asList(0));
        expectedlist.put("doing",Arrays.asList(0,2));
        expectedlist.put("today",Arrays.asList(0,2));
        expectedlist.put("a",Arrays.asList(1,3));
        expectedlist.put("cute",Arrays.asList(1));
        expectedlist.put("dog",Arrays.asList(1,3));
        expectedlist.put("i",Arrays.asList(2));
        expectedlist.put("saw",Arrays.asList(2));
        expectedlist.put("you",Arrays.asList(2));
        expectedlist.put("there",Arrays.asList(2,3));
        Map<Integer, Document> expecteddocuments = new HashMap<Integer, Document>();
        expecteddocuments.put(0,doc0);
        expecteddocuments.put(1,doc1);
        expecteddocuments.put(2,doc2);
        expecteddocuments.put(3,doc3);

        InvertedIndexSegmentForTest expected = new InvertedIndexSegmentForTest(expectedlist,expecteddocuments);
        invertedmanager.mergeAllSegments();
        assertEquals(expected,invertedmanager.getIndexSegment(0));
        invertedmanager.DEFAULT_MERGE_THRESHOLD = 8;
    }

    @Test
    public void test2() {
        InvertedIndexManager invertedmanager;
        invertedmanager = InvertedIndexManager.createOrOpen(path, new ComposableAnalyzer(new PunctuationTokenizer(),new PorterStemmer()));

        Document doc0 = new Document("cat dog");
        Document doc1 = new Document("dog wolf cat");
        Document doc2 = new Document("wolf dog");
        Document doc3 = new Document("wolf cat");
        Document doc4 = new Document("pig wolf cat");
        Document doc5 = new Document("dog pig");
        Document doc6 = new Document("cat wolf");
        Document doc7 = new Document("cat pig dog");
        invertedmanager.addDocument(doc0);
        invertedmanager.addDocument(doc1);
        invertedmanager.flush();
        invertedmanager.addDocument(doc2);
        invertedmanager.addDocument(doc3);
        invertedmanager.flush();
        invertedmanager.addDocument(doc4);
        invertedmanager.addDocument(doc5);
        invertedmanager.flush();
        invertedmanager.addDocument(doc6);
        invertedmanager.addDocument(doc7);
        invertedmanager.flush();

        Map<String,List<Integer>> expectedlist1 = new HashMap<String, List<Integer>>();
        expectedlist1.put("cat",Arrays.asList(0,1,3));
        expectedlist1.put("dog",Arrays.asList(0,1,2));
        expectedlist1.put("wolf",Arrays.asList(1,2,3));
        Map<Integer, Document> expecteddocuments1 = new HashMap<Integer, Document>();
        expecteddocuments1.put(0,doc0);
        expecteddocuments1.put(1,doc1);
        expecteddocuments1.put(2,doc2);
        expecteddocuments1.put(3,doc3);

        Map<String,List<Integer>> expectedlist2 = new HashMap<String, List<Integer>>();
        expectedlist2.put("pig", Arrays.asList(0,1,3));
        expectedlist2.put("wolf",Arrays.asList(0,1));
        expectedlist2.put("cat",Arrays.asList(0,2,3));
        expectedlist2.put("dog",Arrays.asList(1,3));
        Map<Integer, Document> expecteddocuments2 = new HashMap<Integer, Document>();
        expecteddocuments2.put(0,doc4);
        expecteddocuments2.put(1,doc5);
        expecteddocuments2.put(2,doc6);
        expecteddocuments2.put(3,doc7);

        InvertedIndexSegmentForTest expected1 = new InvertedIndexSegmentForTest(expectedlist1,expecteddocuments1);
        InvertedIndexSegmentForTest expected2 = new InvertedIndexSegmentForTest(expectedlist2,expecteddocuments2);
        invertedmanager.mergeAllSegments();
        assertEquals(expected1,invertedmanager.getIndexSegment(0));
        assertEquals(expected2,invertedmanager.getIndexSegment(1));
    }

    @After
    public void clearing(){
        File file = new File(path);
        String[] filelist = file.list();
        for(String f : filelist){
            File temp = new File(path, f);
            temp.delete();
        }
        file.delete();
    }
}
