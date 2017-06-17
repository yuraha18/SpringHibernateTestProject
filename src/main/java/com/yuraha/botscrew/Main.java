package com.yuraha.botscrew;


import com.yuraha.botscrew.view.MainView;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        MainView mainView = new MainView();
        mainView.workWithConsole(context);
        context.close();
    }




}
