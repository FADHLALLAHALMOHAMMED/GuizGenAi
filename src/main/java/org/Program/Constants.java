package org.Program;

public class Constants {
    public static final String databaseURL = "jdbc:mysql://localhost:3306/QuizGenAI";
    public static final String user = "root";
    public static final String password = "system";
    public static final String gemini_api_key = "AIzaSyAX_IwhPDo99uCv6RR_7FMBjGSAqO5z7tU";
    public static final String huggingFaceAPIKey = "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";
    public static final String template = """
            You are a teacher tasked with creating an exam consisting of multiple-choice questions(MCQs) from the documents provided to you.
            
            Generate a maximum of 10 MCQ questions. You can generate less than 10 questions if the document doesn't have enough information.
            The questions should test the student's understanding of the topic discussed in the documents.
            The questions should not ask about trivial document-specific details such as academic year, page numbers, etc.
            Each question must have exactly four choices. Only one of the choices is correct.
            
            Formatting instructions:
            The reply you provided will be automatically parsed by a program, so do not include any text other than the requested questions, answers and tokens.
            
            Add the token "<correct>" after the correct choice to indicate that it is the correct answer.
            Add the token "<question>" directly before the question text.
            Do not enumerate the questions.
            Separate the question and each of the answers with a new line character.
            Do not prepend the answer choices with any alphanumeric symbols for ordering the answer choices: simply separate them with new line characters.
            
            Generate the multiple-choice questions based only on context provided to you.
            """;

    public static String LLMReply = """
            <question>
            What is the challenge in handling corrupted ACK or NAK packets in a communication protocol?
            The sender cannot know if the receiver correctly received the data. <correct>
            The receiver cannot determine if the data is corrupted or a retransmission.
            The protocol cannot detect errors in the ACK/NAK packets.
            The sender cannot resend the data packet.

            <question>
            What is a potential solution to handling corrupted ACK or NAK packets by adding checksum bits?
            The checksum bits detect and correct bit errors, solving the problem for channels that corrupt but don't lose packets. <correct>
            The checksum bits allow the receiver to identify and request retransmission of lost packets.
            The checksum bits eliminate the need for ACK and NAK packets.
            The checksum bits prevent any packets from being corrupted.


            <question>
            What is a drawback of simply resending a data packet upon receiving a corrupted ACK or NAK?
            The receiver may receive duplicate packets and not know if the packet is new data or a retransmission. <correct>
            The sender may get overwhelmed by repeated retransmissions.
            The protocol will become inefficient due to the frequent resending of data.
            The receiver cannot distinguish between corrupted and correctly received data.


            <question>
            In the message-dictation analogy, what does the receiver's "OK" or "Please repeat that" represent?
            Positive and negative acknowledgments. <correct>
            Data packets.
            Checksum bits.
            Error correction codes.


            <question>
            What does ARQ stand for in the context of reliable data transfer protocols?
            Automatic Repeat reQuest. <correct>
            Advanced Reliable Query.
            Asynchronous Request Queue.
            Adaptive Routing Query.


            <question>
            What is the purpose of the acknowledgment number in a TCP connection?
            It indicates the sequence number of the next byte the host expects. <correct>
            It confirms the successful reception of all preceding bytes.
            It identifies the sender of the data segment.
            It verifies the integrity of the received data.


            <question>
            In the analogy of dictating a message over the phone, what action does the speaker take if the receiver doesn't understand a sentence?
            The speaker repeats the garbled sentence. <correct>
            The speaker sends a new message.
            The speaker ends the conversation.
            The speaker ignores the receiver's response.
            """;

    public static final String[] YEARS = {"2025", "2026", "2027", "2028", "2029", "2030"};
    public static final String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    public static final String[] DAYS_29 = {"1",  "2",  "3",  "4",  "5",  "6",  "7",  "8",  "9",  "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29"};
    public static final String[] DAYS_30 = {"1",  "2",  "3",  "4",  "5",  "6",  "7",  "8",  "9",  "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
    public static final String[] DAYS_31 = {"1",  "2",  "3",  "4",  "5",  "6",  "7",  "8",  "9",  "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
    public static final String[] HOURS = {"12 AM", "1 AM", "2 AM", "3 AM", "4 AM", "5 AM", "6 AM", "7 AM", "8 AM", "9 AM", "10 AM", "11 AM",
            "12 PM", "1 PM", "2 PM", "3 PM", "4 PM", "5 PM", "6 PM", "7 PM", "8 PM", "9 PM", "10 PM", "11 PM"};
    public static final String[] MINUTES = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
}
