package com.yuraha.botscrew.view;

import com.yuraha.botscrew.common.ApplicationContextProvider;
import com.yuraha.botscrew.controller.MainController;
import com.yuraha.botscrew.dao.BookDAO;
import com.yuraha.botscrew.common.Constants;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/* this class show whole info on console by working with controller */
public class MainView implements ConsoleWorker{

    public void workWithConsole()
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ApplicationContext appContext = ApplicationContextProvider.getApplicationContext();
        print(Constants.HELLO_MESSAGE);
        try {
            while (true)
            {
                System.out.print("U: ");
                String command = reader.readLine();// read command
                System.out.print("P: ");
                String result = MainController.handleCommand(command, appContext);// send command to controller

                /* if user wanna show all books*/
                if (Constants.OUR_BOOKS.equals(result))
                    MainController.getAllBooks(appContext.getBean(BookDAO.class));

                /* if exit - print goodbye message and get out*/
                else if (Constants.EXIT.equals(result))
                {
                    print(Constants.GOODBYE);
                    break;
                }
                else if (Constants.INFO.equals(result))
                    printInfo();
                else
                    print(result);
                
            }
        }
        catch (Exception e)
        {e.printStackTrace();}

    }

    /* print all info about commands*/
    private void printInfo() {
        print(Constants.COMMANDS_DESCRIPTION);
        print(Constants.ADD_DESC);
        print(Constants.EDIT_DESC);
        print(Constants.REMOVE_DESC);
        print(Constants.ALL_BOOKS_DESC);
        print(Constants.EXIT_DESC);
        print(Constants.INFO_DESC);
    }

    /* print in console whole messages*/
    public static void print(String text)
    {
        System.out.println(text);
    }
}
