package org.Program;

public class Constants {
    public static final String gemini_api_key = "AIzaSyAX_IwhPDo99uCv6RR_7FMBjGSAqO5z7tU";
    public static final String huggingFaceAPIKey = "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";
    public static final String quizTemplate = """
            You are an AI exam creator tasked with generating an assessment based on the provided document.
            
            INSTRUCTIONS:
            1. Create a comprehensive exam with multiple-choice questions (MCQs) and essay questions that assess understanding of key concepts, theories, and applications from the document.
            2. Focus on testing conceptual understanding, critical thinking, and application of knowledge rather than memorization of trivial details.
            3. Do not reference document-specific elements like page numbers, academic years, or formatting.
            4. Do not ask about Specific figures or examples in the document.
            
            OUTPUT REQUIREMENTS:
            - Generate exactly 5 multiple-choice questions (or fewer if there is insufficient material).
            - Generate exactly 2 essay questions (or fewer if there is insufficient material).
            - Provide ONLY the formatted questions and answers as specified below.
            - Do not include any explanatory text, introductions, or conclusions.
            
            FORMATTING RULES:
            For multiple-choice questions:
            <question>Question text goes here
            First answer choice
            Second answer choice
            Third answer choice
            Fourth answer choice<correct>
            
            For essay questions:
            <question>Question text goes here
            
            IMPORTANT NOTES:
            - Place the <question> tag Immediately before the text of each question on the same line .
            - Each MCQ must have exactly four answer choices with only one correct answer.
            - Do not use letters, numbers, or bullets to label questions or answer choices.
            - The <correct> tag must immediately follow (no space) the correct answer choice.
            - Separate all elements with single newline characters.
            - The output must be machine-parsable according to these exact specifications.
            """;

    public static String gradingTemplate = """
            You are an AI teacher tasked with grading students' essay answers using only the information from the provided source document.
                        
            GRADING METHODOLOGY:
            1. Grade each answer strictly based on the content and concepts in the source document.
            2. Assess the answer's accuracy, comprehensiveness, and alignment with the original question.
                        
            GRADING SCALE:
            - <full credit>: Answer demonstrates comprehensive understanding of the topic, accurately reflects key concepts from the document, and fully addresses all aspects of the question.
            - <partial credit>: Answer shows some understanding but:
              * Misses critical points
              * Provides incomplete or partially incorrect information
              * Does not fully address all parts of the question
            - <no credit>: Answer is:
              * Entirely unrelated to the question
              * Contains no relevant information from the document
              * Demonstrates complete misunderstanding of the topic
                        
            GRADING INSTRUCTIONS:
            1. Provide a concise two-sentence justification for the grade.
            2. Focus the justification on how the answer relates to the source document's content.
            3. Be objective and precise in your assessment.
                        
            OUTPUT REQUIREMENTS:
            - Respond only with the formatted grading information
            - No additional text, explanations, or commentary
            - Use exact formatting as specified below
                        
            FORMATTING SPECIFICATIONS:
            <full credit>|Justification explaining why full credit was awarded
            <partial credit>|Justification explaining the gaps in the student's response
            <no credit>|Justification explaining why the answer received no credit
                        
            IMPORTANT NOTES:
            - Grading must be based solely on the source document
            - Justifications must be clear, specific, and no more than two sentences
            - The output must be precisely machine-parsable
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
    public static final String LLMReply1 = """
            <question>What does the /24 notation in an IP address represent?
            A subnet mask indicating the number of bits in the network address
            A range of IP addresses available for hosts
            The total number of IP addresses in the subnet
            The number of hosts allowed on the network<correct>
                        
            <question>How many subnets are present in Figure 4.20?
            One
            Three
            Six<correct>
            Twelve
                        
            <question>What is a subnet in the context of IP addressing?
            A network connecting multiple hosts to a router interface
            An isolated network with interfaces terminating endpoints
            A collection of routers interconnected by point-to-point links
            A single Ethernet segment<correct>
                        
            <question>What is the IP address range for hosts in the 223.1.1.0/24 subnet?
            223.1.1.0 - 223.1.1.255
            223.1.1.1 - 223.1.1.254<correct>
            223.1.1.0 - 223.1.1.254
            223.1.1.1 - 223.1.1.255
                        
            <question>According to the text, what other names are sometimes used for a subnet?
            IP network or simply network<correct>
            Internet segment or LAN segment
            Ethernet network or broadcast network
            Router interface or host interface
                        
            <question>In Figure 4.20, what subnet connects routers R1 and R2?
            223.1.7.0/24
            223.1.8.0/24
            223.1.9.0/24<correct>
            223.1.10.0/24
                        
            <question>What is the significance of the leftmost 24 bits in an IP address with a /24 subnet mask?
            They identify the specific host within the subnet.
            They define the subnet address.<correct>
            They identify the router connected to the subnet.
            They determine the class of the IP address.
                        
            <question>What is Classless Interdomain Routing (CIDR)?
            A method for assigning IP addresses to individual hosts.
            The Internet's address assignment strategy that generalizes subnet addressing.<correct>
            A technique for routing traffic between different subnets.
            A protocol for managing network security.
                        
            <question>What is the significance of the 'x' in the CIDR notation a.b.c.d/x?
            It indicates the number of bits in the network address.<correct>
            It represents the subnet mask.
            It specifies the number of hosts in the subnet.
            It denotes the class of the IP address.
                        
            <question>What method is used to define subnets in a general interconnected system of routers and hosts?
            Detach each interface from its host or router, creating islands of isolated networks.<correct>
            Examine the Ethernet segments and point-to-point links.
            Analyze the routing tables of each router.
            Count the number of hosts and routers in the system.
                        
                        
            <question>Explain the concept of a subnet in the context of IP addressing and provide a real-world example of how subnets might be used within an organization.
            <question>Describe the Classless Interdomain Routing (CIDR) notation and how it relates to subnet addressing.  Give examples to illustrate your explanation.
            <question>Discuss how the identification of subnets changes when considering a network comprised of interconnected routers and point-to-point links, as opposed to a simpler network consisting solely of hosts connected to a single router. Use examples to highlight the differences.
                        
            """;
    public static String sampleReply = """
            <question>What is the primary purpose of assertions in programming?
            Debugging and identifying logic errors
            Improving program performance
            Handling runtime exceptions
            Enhancing user experience<correct>
                        
            <question>How are assertions typically enabled during program execution?
            By default, assertions are always enabled.
            They are enabled through a compiler setting.
            Through a command-line option, such as -ea. <correct>
            Assertions are disabled by default and cannot be enabled.
                        
            <question>What are preconditions and postconditions in the context of assertions?
            Types of runtime exceptions.
            Types of loop structures.
            Types of assertions that specify conditions before and after a method execution. <correct>
            Types of error messages.
                        
            <question>Which statement is true about the impact of assertions on program performance?
            Assertions significantly improve performance.
            Assertions have no impact on performance.
            Assertions slightly improve performance.
            Assertions reduce performance.<correct>
                        
            <question>What happens when an assertion fails?
            The program continues execution without interruption.
            The program terminates with an error message. <correct>
            The program displays a warning message.
            The program pauses execution, awaiting user input.
                        
            <question>What is the purpose of a `try-catch` block?
            To handle potential exceptions during program execution.<correct>
            To define custom exceptions.
            To improve program performance.
            To enhance code readability.
                        
            <question>What is the role of the `finally` block in exception handling?
            To handle specific exceptions.
            To execute code regardless of whether an exception occurred.<correct>
            To prevent exceptions from occurring.
            To display error messages.
                        
            <question>What does a stack trace provide when an exception occurs?
            A list of methods called before the exception.<correct>
            The source code of the program.
            User input data.
            The operating system version.
                        
            <question>What is the purpose of exception handling in a program?
            To prevent exceptions from occurring.
            To gracefully handle unexpected events and errors during program execution.<correct>
            To improve program performance.
            To enhance user interaction.
                        
                        
            <question>Besides using assertions for debugging, what other purpose can they serve?
            Verifying intermediate states within a program during development.<correct>
            Creating user-friendly error messages.
            Improving program speed.
            None of the above.
                        
                        
            <question>What is the syntax of a basic `try-catch` statement in Java (excluding specific exception types)?
            try { ... } catch (Exception e) { ... }<correct>
            try { ... } catch { ... }
            try { ... } finally { ... }
            try { ... } except (e) { ... }
                        
                        
            <question>Describe the difference between preconditions and postconditions in the context of methods.
            Preconditions define the conditions that must be true before a method is called to ensure correct execution, while postconditions specify what must be true after the method completes its execution, indicating its successful outcome and any changes to the program's state.
                        
            <question>Explain the purpose and functionality of the `try`, `catch`, and `finally` blocks within exception handling mechanisms.
            The `try` block encloses the code that may throw an exception.  The `catch` block specifies how to handle specific types of exceptions. The `finally` block guarantees the execution of certain code regardless of whether an exception occurred, often used for cleanup operations like closing resources.
                        
            <question>Discuss the role of assertions in software development, focusing on their benefits and limitations.
            Assertions are valuable debugging tools for detecting logic errors in development. They help ensure program validity by catching potential bugs early in the development cycle, improving code reliability. However, they reduce performance and are not suitable for handling runtime exceptions encountered by end-users. They should be disabled in production environments                        
            """;

    public static String sampleGradingReply = """
            <full credit>|The answer accurately defines a subnet as a division of an IP network and provides a relevant example of departmental subnets within an organization, aligning with the question's request.  The example directly addresses the concept of logical grouping of devices, mirroring the source material's implicit understanding of subnet function.
                        
            <partial credit>|While the answer correctly defines CIDR notation and its purpose in replacing class-based addressing, it lacks the illustrative examples requested in the prompt.  The provided example, although correct in its representation of a subnet, does not demonstrate the CIDR notation's relationship to subnet addressing, as required by the question.
                        
            <partial credit>|The answer partially addresses the question by outlining subnet configurations in simple and complex network scenarios. However, it fails to incorporate concepts or examples from the provided text regarding routers, interfaces, or the specific interaction of subnets with multiple interconnected routers. The provided examples, though accurate, are not extracted from the source document.
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
