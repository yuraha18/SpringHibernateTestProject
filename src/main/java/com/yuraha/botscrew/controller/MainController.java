package com.yuraha.botscrew.controller;

import com.yuraha.botscrew.dao.AuthorDAO;
import com.yuraha.botscrew.dao.BookDAO;
import com.yuraha.botscrew.model.Author;
import com.yuraha.botscrew.model.Book;
import com.yuraha.botscrew.common.Constants;
import com.yuraha.botscrew.common.exceptions.BreakRuntimeException;
import com.yuraha.botscrew.view.MainView;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/* this class handle whole commands and send results in view for showing in console*/
public class MainController {
    public static BufferedReader reader;
    private static AuthorDAO authorDAO;
    private static BookDAO bookDAO;

    /* outside methods can see this method and send commands for handle*/
    public static String handleCommand(String command, ApplicationContext context) {
        reader = new BufferedReader(new InputStreamReader(System.in));
         bookDAO = context.getBean(BookDAO.class);
         authorDAO = context.getBean(AuthorDAO.class);
        return makeCommand(command);
    }

    /* substring commands on single values, and switch commands*/
    private static String makeCommand(String command) {
        String modifiedCommand = command.toLowerCase().trim();
        String result;
        try {
            if (modifiedCommand.startsWith(Constants.ADD))
                result = addBook(command);
            else if (modifiedCommand.startsWith(Constants.REMOVE))
                result = removeBook(command);
            else if (modifiedCommand.startsWith(Constants.EDIT))
                result = editBook(command);
            else if (modifiedCommand.equals(Constants.ALL_BOOKS))
                result = Constants.OUR_BOOKS;
            else if (modifiedCommand.equals(Constants.INFO))
                result = Constants.INFO;
            else if (modifiedCommand.equals(Constants.EXIT))
                result = Constants.EXIT;
            else
                result = Constants.WRONG_COMMAND;
        }
        catch (BreakRuntimeException e1){
            result = Constants.BREAKING;}
        catch (Exception e){
            result =  Constants.WRONG_COMMAND;
        }
        return result;
    }

    /* get al books exist in db*/
    public static void getAllBooks(BookDAO bookDAO) {
        List<Book> allBooksList = bookDAO.getList();
        Collections.sort(allBooksList);//sort list by title, Book impl Comparable

        /* if list empty show message*/
        if (allBooksList.size()==0)
            MainView.print(Constants.THERE_ARE_ANY_BOOKS);

        else {
            MainView.print(Constants.OUR_BOOKS + ":");
            for (Book book : allBooksList)
                MainView.print(bookToString(book));//print books
        }
    }

    private static String editBook(String command) throws IOException {
        int splitSize = Constants.EDIT.length();// get command size for substring
        String bookTitle = command.substring(splitSize).trim();// get book title from command
        List<Book> bookList = bookDAO.getBooksByName(bookTitle);// get list of books with title from command
        Book book = getBook(bookList);
        if (book==null)
            return Constants.BOOK_NOT_EXIST;

        String newTitle = enterTitle();// enter new title
        String oldTitle = book.getTitle();// save old title for showing in info message after saving
        book.setTitle(newTitle);
        bookDAO.edit(book);
        book.setTitle(oldTitle);
        return "book " + bookToString(book) + " was edited";

    }

    /* entering new title for edit book*/
    private static String enterTitle() throws IOException {
        String title;
        /* work till user make break command or enter right title*/
        while (true)
        {
            MainView.print(Constants.ENTER_TITLE);
            title = reader.readLine().trim();
            // check if user enter break command
            checkBreaking(title);

            if (title.length()>=1)
                break;
            else
                MainView.print(Constants.SHORT_TITLE);
        }
        return title;
    }

    /* get book from list, if there are few books - call method choose book*/
    private static Book getBook(List<Book> bookList) throws IOException {
        if (bookList.size() ==0)
            return null;
        else if (bookList.size() == 1)
            return bookList.get(0);
        else
            return chooseBook(bookList);
    }

    private static String removeBook(String command) throws IOException {
        int splitSize = Constants.REMOVE.length();
        String bookTitle = command.substring(splitSize).trim();
        List<Book> bookList = bookDAO.getBooksByName(bookTitle);// get list of books with command title
        Book book = getBook(bookList);
        if (book==null)
            return Constants.BOOK_NOT_EXIST;

        bookDAO.remove(book);
        return "book " + bookToString(book) + " was removed";
    }

    /*if there are few books with the same title in db user must choose which one he need */
    private static Book chooseBook(List<Book> bookList)  {
        int i =1;
        MainView.print(Constants.THERE_ARE_FEW_BOOKS);
        for (Book book : bookList)
        {
            MainView.print(i + ". " + bookToString(book));
            i++;
        }
        int id = getBookIdByCommand(i);// enter number book
        return bookList.get(id-1);
    }

    private static int getBookIdByCommand(int lastIndex) {
        int id;
        /* work till user make break command or choose right number*/
        while (true) {
            try {
                String command = reader.readLine();
                checkBreaking(command);

                id = Integer.parseInt(command);
                if (id >= lastIndex || id<=0)// if number is bigger or less than count of books
                    throw new NumberFormatException();

                break;// break from loop if user enter right number
            } catch (NumberFormatException e) {
                //if command ist number and break command
                MainView.print(Constants.WRONG_NUMBER);
            }
            catch (IOException e){/*NOP*/}
        }
        return id;
    }

    /* check if command break*/
    private static void checkBreaking(String command) {
        if (Constants.BREAK.equals(command.toLowerCase().trim()))
            throw new BreakRuntimeException();
    }

    private static String addBook(String command) {
        int splitSize = Constants.ADD.length();
        if (countOfQuoteMarks(command)!=2)// true command must have only 2 " which describe start and end name of book
            return Constants.WRONG_TITLE_OR_AUTHOR;

        String modifiedCommand = command.trim().substring(splitSize);
        String authorName = modifiedCommand.substring(0, modifiedCommand.indexOf("\"")).trim();
        String title = modifiedCommand.substring(modifiedCommand.indexOf("\"")+1, modifiedCommand.lastIndexOf("\""));

        if (authorName.length()<=1 || title.length()<=1)
            return Constants.WRONG_TITLE_OR_AUTHOR;

        Author author = new Author();
        author.setName(authorName);
        return saveBook(author, title);// save book in db

    }

    private static String saveBook(Author author, String title) {
        try {
            long authorId = authorDAO.save(author).getId();// get author id
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(authorId);

            /* if there is the same book title and its author in db show message and get out*/
            if (bookDAO.isExist(book))
                return Constants.SAME_BOOK;

            bookDAO.save(book);
            return "book " + bookToString(book) +" was added " ;
        }
        catch (Exception e)
        {
            return Constants.PROBLEMS_WITH_SAVING;}
    }

    private static int countOfQuoteMarks(String command) {
        int occurrences = 0;
        for(char c : command.toCharArray()){
            if(c == '"'){
                occurrences++;
            }
        }
        return occurrences;
    }

    /* return string with book values*/
    private static String bookToString(Book book)
    {
        String author = authorDAO.getNameById(book.getAuthor());
        String title = book.getTitle();
        return author + " \"" + title +"\"";
    }
}
