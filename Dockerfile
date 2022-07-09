FROM openjdk
WORKDIR chat
ADD target/job4j_chat-0.0.1-SNAPSHOT.jar chat.jar
ENTRYPOINT java -jar chat.jar