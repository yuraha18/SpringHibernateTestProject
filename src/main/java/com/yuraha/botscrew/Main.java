package com.yuraha.botscrew;

import com.yuraha.botscrew.view.ConsoleWorker;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        ConsoleWorker cw = (ConsoleWorker) context.getBean("consoleWorker");
        cw.workWithConsole();
        context.close();
    }
}
