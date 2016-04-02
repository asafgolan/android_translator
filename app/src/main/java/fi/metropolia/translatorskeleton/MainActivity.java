package fi.metropolia.translatorskeleton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Scanner;

import fi.metropolia.translatorskeleton.model.HardQuiz;
import fi.metropolia.translatorskeleton.model.Quiz;
import fi.metropolia.translatorskeleton.model.QuizItem;
import fi.metropolia.translatorskeleton.model.RandomQuiz;
import fi.metropolia.translatorskeleton.model.TimeOutQuestion;
import fi.metropolia.translatorskeleton.model.UserData;
import fi.metropolia.translatorskeleton.model.MyModelRoot;
import fi.metropolia.translatorskeleton.model.User;
import fi.metropolia.translatorskeleton.model.Dictionary;
import fi.metropolia.translatorskeleton.model.DictionaryController;



public class MainActivity extends AppCompatActivity {
    private static int TIMEOUT = 5000;//five secs timeout
    private boolean timeIsOut;
    private Scanner sc;



    public void doQuiz(Quiz q , User user) {

        System.out.println("FROM DO QUIZ");
        for (int i = 0; i < q.getQuizLength(); i++) {
            System.out.println("FROM THE LOOP");
            System.out.println(q.getItem(i).getQuestion());
           // QuizItem item = q.getItem(i);
            System.out.print(q.getItem(i).getQuestion() + "?");

            //set timeout
            //TimeOutQuestion toq = new TimeOutQuestion(TIMEOUT);
           // toq.registerTimeOutObserver(this);
            //Thread t = new Thread(toq);
            //t.start();
            timeIsOut = false;

            String answer = "";
            if(i == 0){
                 answer = "moi";
            }
            if(i==1){
                answer = "kauppa";
            }

            //kill the timeout thread
            /**t.interrupt();**/

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UserData u = MyModelRoot.getInstance().getUserData();
        u.setUser(new User("Myself"));
        u.addDictionary("fineng", new Dictionary("fin", "eng"));
        u.addDictionary("engfin", new Dictionary("eng", "fin"));

        /**System.out.println("FROM ON CREATE");
        u.getDictionary("engfin").addPair("hello", "moi");
        u.getDictionary("engfin").addPair("shop", "kauppa");
        //System.out.println(MyModelRoot.getInstance().getUserData().getDictionary("engfin"));
        Quiz engfinQuiz = new RandomQuiz(2,MyModelRoot.getInstance().getUserData().getDictionary("engfin"));
        doQuiz(engfinQuiz, u.getUser());
        System.out.println("GETTING TRACK RECORD");
        System.out.println(u.getUser().getTrackRecord());
        Quiz engfinHArdQuiz = new HardQuiz(1,u.getDictionary("engfin"),u.getUser().getTrackRecord() );
        doQuiz(engfinHArdQuiz, u.getUser());**/

    }
}
