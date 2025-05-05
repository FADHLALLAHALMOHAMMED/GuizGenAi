package org.Program;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.Program.Entities.Question;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.List;
import java.util.Vector;

import static java.time.Duration.ofSeconds;
public class QuizGen implements Runnable{
    public static String filePath;
    public static Window window;
    public static Vector<Question> questions;
    public static String documentHash;
    public static int mode;

    /**
     *  basic Terminology:
     *      Document Parser: a function that reads a document, extracts its text, and passes the text to the code.
     *      embeddings: since a large language models can't directly understand human language, text must first be "embedded"
     *          which means that the text is turned into a vector of number that are meaningful to the LLM, which are called "embeddings"
     *      content retriever / vector store: stores the embeddings and allow for "Cosine Similarity Search:
     *      Cosine Similarity Search: when documents are embedded, you can search for parts of the document that are relevant to your question.
     *          e.g., Rather than passing the entire document to the LLM when we want to ask is a question, cosine similarity search
     *          finds the parts of the document relevant to your question and passes only those to the LLM, which is much more efficient.
     *      Document splitter: we split the document in to parts, each part is going to have its own embedding, so that
     *          we can search through them using Cosine Similarity Search.
     *      Hashing: (not relevant to the LLM in this code) you can hash documents to get a unique "key", hashing the same
     *          document will always return the same key, hashing different document returns *different keys.
     *          in this program we store hash and compare it with the hash of new documents to see if we embedded them before
     *          since the embedding operation is expensive and for efficiency, we don't want to embed something that we embedded before.
     */
    @Override
    public void run(){
        for(int retries = 0; retries < 3; retries++) {
            try {
                JProgressBar progressBar = ((WaitingPage) window.getPage()).progressBar;
                ContentRetriever contentRetriever = getVectorStore(filePath); // get the embedding of the document.

                if (mode == 1) { // if the mode is 1, return a genuine reply from the users document.

                    progressBar.setValue(50);
                    // define the LLM Model
                    ChatLanguageModel chatModel = GoogleAiGeminiChatModel.builder()
                            .apiKey(Constants.gemini_api_key)
                            .modelName("gemini-1.5-flash")
                            .build();

                    progressBar.setValue(55);

                    interface Assistant {
                        String chat(String userMessage);
                    }

                    // define the pipe line.
                    Assistant assistant = AiServices.builder(Assistant.class)
                            .chatLanguageModel(chatModel)
                            .chatMemory(MessageWindowChatMemory.withMaxMessages(1))
                            .contentRetriever(contentRetriever)
                            .build();
                    progressBar.setValue(60);

                    // ask the chat model to generate the quiz.
                    String reply = assistant.chat(Constants.quizTemplate);
                    progressBar.setValue(90);

                    // parse the reply the get the questions.
                    questions = Question.process(reply);
                    System.out.println(reply);
                    progressBar.setValue(95);

                } else { // if the mode is not 1, return a sample quiz.
                    int value = 0;
                    for (int i = 0; i < 20; i++) {
                        Thread.sleep(50);
                        progressBar.setValue(value);
                        value += 5;
                    }

                    questions = Question.process(Constants.sampleReply);
                }

                // switch the page to the edit question Page.
                window.switchPage(new EditQuizPage(window));
                filePath = null;
                return;

            } catch (Exception e) {
                if (e.getMessage().matches("503")) {
                    // control will reach here if the embedding API fails.
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    System.out.printf("Embedding service unavailable: retrying(%d/3) \n", retries+1);
                    // if the API fails the loop will try 2 more times, then give up and show an error message.
                } else {
                    // control will reach here in case of unexpected error or user pressing cancel in waiting Page.
                    System.out.println("Thread1 Successfully interrupted: ");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    window.switchPage(new InstructorHomePage(window));
                    filePath = null;
                    return;
                }
            }
        }
        HelperFunctions.showDialogIfError("Embedding service unavailable.", window);
        filePath = null;
        window.switchPage(new InstructorHomePage(window));
    }

    /**
     * Gets the hash of a file.
     * @param filepath String containing filepath whose hash we want.
     * @return the Hash in the form of a String of length 44.
     */
    public static String getHash(String filepath){
        byte[] buffer= new byte[8192]; // reserve 8kb in memory
        MessageDigest digest;
        int count;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filepath))) {

            digest = MessageDigest.getInstance("SHA-256"); // 'SHA-265' is the name of the hashing method.
            while ((count = bis.read(buffer)) > 0)          // read 8kb at a time.
                digest.update(buffer, 0, count);      // digest the data we read (hashing)

            byte[] hash = digest.digest();                  // final operations
            return Base64.getEncoder().encodeToString(hash); // translate the data into a string
            // the base 64 encoder encodes every 6 bits into a char(64 possible chars),
            // so, the 32 bytes returned from the hash will turn into a 44 character long string.

        }catch(Exception e){
            throw new RuntimeException("File Hashing function failed");
        }
    }

    /**
     * This function is only called if there is no embedding stored in the database for this document.
     * it creates new embeddings and stores them in the database.
     * @param filepath: the filepath to the document to be embedded.
     * @param hash: the hash of the document.
     * @param embeddingModel: the embedding model that will embed the document.
     * @return a ContentRetriever that has the embeddings of this document.
     */
    public static ContentRetriever createVectorStore(String filepath, String hash, EmbeddingModel embeddingModel){
//        try {
            // todo: The parsed text has a lot of copyright labels e.g., "© COPYRIGHT 1992-2015 BY PEARSON EDUCATION,INC. ALL RIGHTS RESERVED."
            //  see if there is a setting in the parser to remove them can be removed. regardless, LLM output is satisfactory even with them.
            Document document = FileSystemDocumentLoader.loadDocument(filepath, new ApacheTikaDocumentParser()); // parse the document, get the text
            DocumentSplitter splitter = new DocumentByWordSplitter(1000, 200);
            List<TextSegment> splits = splitter.split(document);// split the text

            InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>(); // create an embedding store
            Response<List<Embedding>> embeddings = embeddingModel.embedAll(splits); // have the model embed the text splits
            embeddingStore.addAll(embeddings.content(), splits); // store the embeddings
            Database.storeEmbeddings(embeddingStore, hash); // store the embeddings along with the hash in the database.

            return EmbeddingStoreContentRetriever.builder() // create, and return the content retriever.
                    .embeddingStore(embeddingStore)
                    .embeddingModel(embeddingModel)
                    .maxResults(5)
                    .minScore(0.0)
                    .build();

//        }catch(Exception e){
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("VectorStore creation failed.");
//        }
    }

    /**
     * Since embedding a document is expensive (and for the sake of my api key), we store the hashes and embeddings of
     * the documents that we embedded in the database.
     * when the user wants to create a new quiz, before embedding his document, we see if the document has been embedded before:
     *  First we get the hash of his document (32 bytes -> 44 chars), and search for a match in the database.
     * if there is a match, we return the previous embeddings.
     * if there isn't, we embed the document, and store the hash along with the embeddings in the database, then return the embeddings.
     * @param filepath String containing filepath to the document whose embeddings we want.
     * @return A ContentRetriever that contains the embeddings of the document.
     */
    public static ContentRetriever getVectorStore(String filepath){
        try {
//            EmbeddingModel embeddingModel = HuggingFaceEmbeddingModel.builder()
//                    .accessToken(Constants.huggingFaceAPIKey)
//                    .modelId("sentence-transformers/all-MiniLM-L6-v2")
//                    .waitForModel(true)
//                    .timeout(ofSeconds(60))
//                    .build();
            EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

            String hash = getHash(filepath); // get the hash of the document
            QuizGen.documentHash = hash;
            InMemoryEmbeddingStore<TextSegment> embeddings = Database.retrieveEmbeddings(hash); // search for a match in the database.
            if (embeddings != null) // if not null, then a match was found
                return EmbeddingStoreContentRetriever.builder() // return match
                        .embeddingStore(embeddings)
                        .embeddingModel(embeddingModel)
                        .maxResults(5)
                        .minScore(0.0)
                        .build();

            return createVectorStore(filepath, hash, embeddingModel); // if not match was found generate the embeddings

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Grades the student's answers for the Essay questions using the LLM, the prompt String, and the embedding of the quiz's document.
     * @param submissionId the submissionId of the submission we want to grade.
     * @param prompt a String containing the Essay Question, the student's answers, and Instructions to the LLM for Grading.
     * @return The reply of the LLM as a String that contains each question's grades and justifications for each grade.
     */
    public static String gradeEssay(int submissionId, String prompt){
        for(int retries = 0; retries < 3; retries++) { // retry to do this 3 times.
            try {
                String embeddingsJSON = Database.getEmbeddingsBySubmission(submissionId); // get the embeddings of the quiz.
                if (embeddingsJSON == null) {
                    System.out.println("No embeddings found for the Quiz: the essay questions can't be graded.");
                    return null;
                }
                System.out.println(prompt);

                EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
                System.out.println("reached point 2");

                ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder() // return match
                        .embeddingStore(InMemoryEmbeddingStore.fromJson(embeddingsJSON))
                        .embeddingModel(embeddingModel)
                        .maxResults(5)
                        .minScore(0.0)
                        .build();

                ChatLanguageModel chatModel = GoogleAiGeminiChatModel.builder()
                        .apiKey(Constants.gemini_api_key)
                        .modelName("gemini-1.5-flash")
                        .build();

                System.out.println("reached point 3");

                interface Assistant {
                    String chat(String userMessage);
                }
                Assistant assistant = AiServices.builder(Assistant.class)
                        .chatLanguageModel(chatModel)
                        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                        .contentRetriever(contentRetriever)
                        .build();
                System.out.println("reached point 4");
                //        return Constants.sampleGradingReply;
                return assistant.chat(prompt);

            } catch (Exception e) {
                if (e.getMessage().matches("503")) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    // control will reach here if the embedding API fails.
                    System.out.printf("Embedding service unavailable: retrying(%d/3) \n", retries+1);
                    // if the API fails the loop will try 2 more times, then give up.
                } else {
                    // control will reach here in case of an unexpected error.
                    System.out.println("Grading Exception");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
