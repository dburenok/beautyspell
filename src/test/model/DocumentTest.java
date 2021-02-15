package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    private Document testDocument;
    private DocumentLibrary testDocLib;

    @BeforeEach
    void runBefore() {}

    @Test
    void testConstructor() {
        String text = "This is some text.";
        testDocument = new Document(text);
        assertEquals(text, testDocument.getText());
        assertNotNull(testDocument);
    }

    @Test
    void testConstructorEmptyStringGetText() {
        String text = "";
        testDocument = new Document(text);
        assertNotNull(testDocument);
        assertNull(testDocument.getText());
    }

    @Test
    void testCleanWhitespace() {
        String text = " This is a nice freakin' car  right here , boss . Where's that 200 dollar car? 40% off, right? ";
        testDocument = new Document(text);
        String correctText = "This is a nice freakin' car right here, boss. Where's that 200 dollar car? 40% off, right?";
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        String result = testDocument.getText();
        assertEquals(correctText, result);
    }

    @Test
    void testCleanPunctuationWhitespace() {
        String text = "Some messy , weird  ,  text right here man . ";
        testDocument = new Document(text);
        String correctText = "Some messy, weird, text right here man.";
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        String result = testDocument.getText();
        assertEquals(correctText, result);
    }

    @Test
    void testBreakTextIntoArray() {
        String text = "Here is some text, it needs breaking up.";
        testDocument = new Document(text);
        ArrayList<String> wordList = new ArrayList<>(
                Arrays.asList("Here", " ", "is", " ", "some", " ", "text", ",", " ", "it", " ", "needs", " ", "breaking", " ", "up", "."));
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        assertEquals(wordList, testDocument.getWordsArray());
    }

    @Test
    void testBreakTextIntoArrayAndPutBackTogether() {
        String text = "Here is some text, it needs breaking up and putting back together! Do it.";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        assertEquals(text, testDocument.putWordArrayBackTogether());
    }

    @Test
    void testNoSpellingErrors() throws FileNotFoundException {
        String text = "This sentence has no typos.";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        testDocument.runSpellcheck();
        assertEquals(0, testDocument.numErrors());
    }

    @Test
    void testTwoSpellingErrors() throws FileNotFoundException {
        String text = "Thiss sentence haz two typos.";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        testDocument.runSpellcheck();
        assertEquals(2, testDocument.numErrors());
    }

    @Test
    void testFourSpellingErrors() throws FileNotFoundException {
        String text = "Thiss sentenc haz four typoss.";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        testDocument.runSpellcheck();
        assertEquals(4, testDocument.numErrors());
    }

    @Test
    void testOneWordNoError() throws FileNotFoundException {
        String text = "oats";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        assertFalse(testDocument.showErrors());
        testDocument.runSpellcheck();
        assertTrue(testDocument.showErrors());
        assertEquals(0, testDocument.numErrors());
        assertEquals(1, testDocument.breakTextIntoWordArray().size());
        assertEquals(text, testDocument.getText());
    }

    @Test
    void testOneWordWithError() throws FileNotFoundException {
        String text = "oatz";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        assertFalse(testDocument.showErrors());
        testDocument.runSpellcheck();
        assertTrue(testDocument.showErrors());
        assertEquals(1, testDocument.numErrors());
        assertEquals(1, testDocument.breakTextIntoWordArray().size());
        assertEquals(text, testDocument.getText());
        assertEquals("oatz", testDocument.getNextError().getTypoText());
    }

    @Test
    void testReplaceText() throws FileNotFoundException {
        String text = "Some text here.";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        assertFalse(testDocument.showErrors());
        testDocument.runSpellcheck();
        assertTrue(testDocument.showErrors());
        assertEquals(0, testDocument.numErrors());
        assertEquals(6, testDocument.breakTextIntoWordArray().size());
        assertEquals(text, testDocument.getText());
        text = "Now we get new text!";
        testDocument.replaceText(text);
        assertTrue(testDocument.showErrors());
        assertEquals(0, testDocument.numErrors());
        assertEquals(10, testDocument.breakTextIntoWordArray().size());
        assertEquals(text, testDocument.getText());
    }

    @Test
    void testDocumentLibrary() throws FileNotFoundException {
        String text = "Some text.";
        testDocument = new Document(text);
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        assertEquals(1, testDocLib.numDocuments());
        assertEquals(testDocument, testDocLib.getLastDocument());
    }

    @Test
    void testTypoPositions() throws FileNotFoundException {
        String text = "i haz stuff";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        testDocument.runSpellcheck();
        assertTrue(testDocument.hasErrors());
        SpellingError e = testDocument.getNextError();
        assertEquals(2, e.typoPositionStart());
        assertEquals(5, e.typoPositionEnd());
    }

    @Test
    void longTextWithTypo() throws FileNotFoundException {
        String text = "This is going to be a very long text and a typo will appear heeere. Now that we have made the " +
                "typo, we will continue to write in here.";
        testDocument = new Document(text);
        testDocument.fixWhitespace();
        testDocument.fixPunctuationWhitespace();
        testDocument.breakTextIntoWordArray();
        testDocLib = new DocumentLibrary();
        testDocLib.addDocument(testDocument);
        testDocument.runSpellcheck();
        SpellingError e = testDocument.getNextError();
        e.showError(testDocument.getText());
        assertTrue(testDocument.hasErrors());
    }

}







