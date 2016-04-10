package fi.metropolia.translatorskeleton.model;

/**
 * Created by petrive on 23.3.16.
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author petrive
 *
 * Dictionary controller has the main logic of the application
 * as well as the UI.
 */
public class DictionaryController implements TimeOutObserver {


    private User user;
    private Scanner sc;
    private Dictionary dictEngFin;
    private Dictionary dictFinEng;
    private static int TIMEOUT = 5000;//five secs timeout
    private boolean timeIsOut;

    public DictionaryController() {
        sc = new Scanner(System.in);
        refresh();
    }

    private void refresh() {
        user = MyModelRoot.getInstance().getUserData().getUser();
        dictEngFin = MyModelRoot.getInstance().getUserData().getDictionary("engfin");
        dictFinEng = MyModelRoot.getInstance().getUserData().getDictionary("fineng");
    }

    void start() {
        System.out.println("Lets Start learning words\n\n");
        boolean done = false;

        do {
            System.out.print("> ");
            String input = sc.nextLine();

            if (input.equalsIgnoreCase("add")) {
                // start adding words
                addWords();
            } else if (input.equals("delete")) {
                deleteWords();
            } else if (input.equalsIgnoreCase("print")) {
                System.out.println(dictEngFin);
                System.out.println(dictFinEng);
            } else if (input.equalsIgnoreCase("random quiz")) {
                doQuiz(new RandomQuiz(4, dictEngFin));
            } else if (input.equalsIgnoreCase("hard quiz")) {
                doQuiz(new HardQuiz(4, dictEngFin, user.getTrackRecord()));
            } else if (input.equalsIgnoreCase("status")) {
                doStatus();
            } else if (input.equals("save")) {
                //saveDictionary(dictEngFin, "dictengfin.ser");
                //saveDictionary(dictFinEng, "dictfineng.ser");
                //saveUser("quizuser.ser");
                save("quiz.ser");
            } else if (input.equals("reload")) {
                //this.dictEngFin = reloadDictionary("dictengfin.ser");
                //this.dictFinEng = reloadDictionary("dictfineng.ser");
                //this.user = reloadUser("quizuser.ser");
                MyModelRoot.getInstance().setUserData(reload("quiz.ser"));
                refresh();
            } else if (input.equals("init")) {
                initDictionaryFromFile("engFinDictionary.txt");
            } else if (input.equalsIgnoreCase("quit")) {
                done = true;
            } else {
                System.out.println("Sorry I did not understand.");
            }
        } while (!done);
    }

    public void doQuiz(Quiz q) {
        for (int i = 0; i < q.getQuizLength(); i++) {
            QuizItem item = q.getItem(i);
            System.out.print(item.getQuestion() + "?");

            //set timeout
            TimeOutQuestion toq = new TimeOutQuestion(TIMEOUT);
            System.out.println(this);
            //toq.registerTimeOutObserver(this);
            Thread t = new Thread(toq);
            t.start();
            timeIsOut = false;

            String answer = sc.nextLine();

            //kill the timeout thread
            t.interrupt();

            if (timeIsOut) {
                System.out.print("Time ran out. ");
                answer = null;
            }

            if (q.checkAnswer(i, answer)) {
                System.out.println("Correct answer!");
            } else {
                System.out.println("You did not get it right this time.");
            }
        }
        user.addQuiz(q);
    }

    private void addWords() {
        while (true) {
            System.out.print(dictEngFin.getLang1() + ": ");
            String fromWord = sc.nextLine();
            if (fromWord.equals("!")) {
                break;
            }
            if (fromWord.matches("[a-zA-Z ]+")) {
                System.out.print(dictEngFin.getLang2() + ": ");
                String toWord = sc.nextLine();
                if (toWord.matches("[\\pL ]+")) {
                    dictEngFin.addPair(fromWord, toWord);
                    dictFinEng.addPair(toWord, fromWord);
                } else {
                    System.out.println("Invalid input");
                }
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    private void deleteWords() {
        while (true) {
            System.out.print(dictEngFin.getLang1() + ": ");
            String word1 = sc.nextLine();
            if (word1.equals("!")) {
                break;//HYI!!!
            }
            if (word1.matches("[a-zA-Z ]+")) {
                System.out.print(dictEngFin.getLang2() + ": ");
                String word2 = sc.nextLine();
                if (word2.matches("[\\pL ]+")) {
                    dictEngFin.deletePair(word1, word2);
                    dictFinEng.deletePair(word2, word1);
                } else {
                    System.out.println("Invalid input");
                }
            } else {
                System.out.println("Invalid input.");
            }
        }
    }

    private void saveDictionary(Dictionary d, String filename) {
        FileOutputStream fileout;
        ObjectOutputStream objout;

        try {
            fileout = new FileOutputStream(filename);
            objout = new ObjectOutputStream(fileout);
            objout.writeObject(d);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Dictionary reloadDictionary(String filename) {
        FileInputStream filein;
        ObjectInputStream objin;
        Dictionary d = null;

        try {
            filein = new FileInputStream(filename);
            objin = new ObjectInputStream(filein);
            d = (Dictionary) objin.readObject();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return d;
    }

    private void initDictionaryFromFile(String filename) {
        try {
            String line;
            String[] words = new String[2];
            Scanner scf = new Scanner (new FileInputStream(filename));
            //Scanner scf = new Scanner(filename);//
            int counter = 0;

            System.out.println("Reading file "+filename+":");

            while (scf.hasNext()) {
                counter++;
                line = scf.nextLine();
                System.out.println(line);
                words = line.split(",");

                //remove leading and trailing spaces.
                words[0] = words[0].trim();
                words[1] = words[1].trim();
                System.out.println(counter+": "+line);
                //add to a dictionary
                if (words[0].matches("[a-zA-Z ]+")&&words[1].matches("[\\pL ]+")) {
                    dictEngFin.addPair(words[0], words[1]);
                    dictFinEng.addPair(words[1], words[0]);
                } else {
                    System.out.println("Invalid input:"+line);
                }
            }

            scf.close();
            System.out.println("Finished reading file. "+counter+" entries created");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void timeout() {
        //this method is called by the timeout thread
        //closing the scanner will cause illegalstateexception
        //lets write a new line to System.
        timeIsOut = true;
    }

    private void doStatus() {
        System.out.println(user.getTrackRecord());
    }

    private void saveUser(String filename) {
        FileOutputStream fileout;
        ObjectOutputStream objout;

        try {
            fileout = new FileOutputStream(filename);
            objout = new ObjectOutputStream(fileout);
            objout.writeObject(user);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private User reloadUser(String filename) {
        FileInputStream filein;
        ObjectInputStream objin;
        User u = null;

        try {
            filein = new FileInputStream(filename);
            objin = new ObjectInputStream(filein);
            u = (User) objin.readObject();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return u;
    }

    private void save(String filename) {
        FileOutputStream fileout;
        ObjectOutputStream objout;

        try {
            fileout = new FileOutputStream(filename);
            objout = new ObjectOutputStream(fileout);
            objout.writeObject(MyModelRoot.getInstance().getUserData());
            fileout.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //done
    private UserData reload(String filename) {
        FileInputStream filein;
        ObjectInputStream objin;
        UserData u = null;

        try {
            filein = new FileInputStream(filename);
            objin = new ObjectInputStream(filein);
            u = (UserData) objin.readObject();
            filein.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(DictionaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return u;
    }
}
