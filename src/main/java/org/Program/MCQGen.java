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
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.Program.Entities.Question;

import java.util.List;
import java.util.Vector;

import static java.time.Duration.ofSeconds;
public class MCQGen implements Runnable{
    public static String filePath;
    public static Window window;
    public static Vector<Question> questions;

    @Override
    public void run(){
        try {
            WaitingPage page = (WaitingPage) window.getPage();
//
//            page.progressBar.setValue(5);
//            EmbeddingModel embeddingModel = HuggingFaceEmbeddingModel.builder()
//                    .accessToken(Constants.huggingFaceAPIKey)
//                    .modelId("sentence-transformers/all-MiniLM-L6-v2")
//                    .waitForModel(true)
//                    .timeout(ofSeconds(60))
//                    .build();
//
//            ChatLanguageModel chatModel = GoogleAiGeminiChatModel.builder()
//                    .apiKey(Constants.gemini_api_key)
//                    .modelName("gemini-1.5-flash")
//                    .build();
//
//            page.progressBar.setValue(10);
//            InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>(); // there are two text segment classes: one from apache other from LangChain
//
//            page.progressBar.setValue(15);
//            Document document = FileSystemDocumentLoader.loadDocument(filePath, new ApacheTikaDocumentParser());
//            System.out.println("Loaded document successfully");
//
//            page.progressBar.setValue(20);
//            DocumentSplitter splitter = new DocumentByWordSplitter(1000, 200);
//
//            page.progressBar.setValue(35);
//            List<TextSegment> splits = splitter.split(document);
//            System.out.println("Document split into " + splits.size() + " segments");
//
//            page.progressBar.setValue(50);
//            Response<List<Embedding>> embeddings = embeddingModel.embedAll(splits);
//            System.out.println("Generated embeddings: " + embeddings.content().size());
//
//            page.progressBar.setValue(70);
//            embeddingStore.addAll(embeddings.content(), splits);
//
//            page.progressBar.setValue(85);
//            ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
//                    .embeddingStore(embeddingStore)
//                    .embeddingModel(embeddingModel)
//                    .maxResults(5)
//                    .minScore(0.0)
//                    .build();
//
//            interface Assistant {
//                String chat(String userMessage);
//            }
//
//            Assistant assistant = AiServices.builder(Assistant.class)
//                    .chatLanguageModel(chatModel)
//                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                    .contentRetriever(contentRetriever)
//                    .build();
//
//            String reply = assistant.chat(Constants.template);
//            questions = Question.process(reply);
//            window.switchPage(new EditQuizPage(window));

            int value = 0;
            for(int i = 0; i < 20; i++){
                Thread.sleep(50);
                page.progressBar.setValue(value);
                value += 5;
            }

            questions = Question.process(Constants.LLMReply);
            window.switchPage(new EditQuizPage(window));

        } catch(Exception e){
            System.out.println("Thread1 Successfully interrupted: " + e);
        } finally{
            filePath = null;
        }
    }
}
