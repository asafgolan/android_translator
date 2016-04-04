package fi.metropolia.translatorskeleton;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import fi.metropolia.translatorskeleton.model.HardQuiz;
import fi.metropolia.translatorskeleton.model.Quiz;
import fi.metropolia.translatorskeleton.model.QuizItem;
import fi.metropolia.translatorskeleton.model.RandomQuiz;
import fi.metropolia.translatorskeleton.model.TimeOutQuestion;
import fi.metropolia.translatorskeleton.model.UserData;
import fi.metropolia.translatorskeleton.model.MyModelRoot;
import fi.metropolia.translatorskeleton.model.User;
import fi.metropolia.translatorskeleton.model.Dictionary;



public class MainActivity extends AppCompatActivity {
    private static int TIMEOUT = 5000;//five secs timeout
    private static boolean timeIsOut;
    Quiz currQuiz;
    User user;
    UserData u = MyModelRoot.getInstance().getUserData();
    private boolean FragmentIsReady = false;
    private boolean answarSubmitted =  false;
    private TextView questionTv;
    int itemCounter = 0;

    public void setFragmentisReady(boolean bool){
        FragmentIsReady = bool;
    }



    public void askQuestion (){
        System.out.println("QUIZ LENGHT:");
        System.out.println(currQuiz.getQuizLength());
        System.out.println("ITEM COUNTER");
        System.out.println(itemCounter);
        if(itemCounter <= currQuiz.getQuizLength()) {
            if (FragmentIsReady) {
                QuizItem item = currQuiz.getItem(itemCounter);
                String question = (itemCounter + 1) + ". " + item.getQuestion() + "?";
                questionTv = (TextView) findViewById(R.id.question);
                questionTv.setText(question);
            }
        }else{

            user.addQuiz(currQuiz);
        }
    }

    public void verifyAnswer(View view){
        EditText answerET = (EditText)findViewById(R.id.answer);
        String answer = answerET.getText().toString();
        if (currQuiz.checkAnswer(itemCounter, answer)) {
            System.out.println("Correct answer!");
        }else{
            System.out.println("You did not get it right this time.");
        }
        itemCounter++;
        if(itemCounter < currQuiz.getQuizLength()){
            askQuestion();
        }else{
            user.addQuiz(currQuiz);
            System.out.println("FINISHED QUIZ");
            Context context = getApplicationContext();

            currQuiz.getItems();
            CharSequence text = "Finished the quiz.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

   /** public void doQuiz (Quiz q, User user) {
        System.out.println("FROM DO QUIZ");
        //without the if statement there is no frament change


        if (FragmentIsReady) {
            for (int i = 0; i < q.getQuizLength(); i++) {
                QuizItem item = q.getItem(i);

                String question = (i + 1) + ". " + item.getQuestion() + "?";

                questionTv = (TextView)findViewById(R.id.question);
                questionTv.setText(question);
                //set timeout
                //TimeOutQuestion toq = new TimeOutQuestion(TIMEOUT);
                //toq.registerTimeOutObserver(this);
                //Thread t = new Thread(toq);
                //t.start();
                timeIsOut = false;
                EditText answerET = (EditText)findViewById(R.id.answer);
                String answer = answerET.getText().toString();

                    //kill the timeout thread
                    //t.interrupt();

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
    }**/
    public void doRandQuiz(View view){
        System.out.println("FROM DO RAND QUIZ");
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.listcontainer, new TakeQuizFragment());
        ft.commit();
        //makes transaction sync
        fm.executePendingTransactions();

        currQuiz = new RandomQuiz(2, MyModelRoot.getInstance().getUserData().getDictionary("engfin"));
        this.user =  u.getUser();
        askQuestion();
        //doQuiz(engfinQuiz, u.getUser());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //add fragments
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.listcontainer, new ChooseQuizFragment());
        ft.commit();
        //UserData u = MyModelRoot.getInstance().getUserData();
        u.setUser(new User("Myself"));
        u.addDictionary("fineng", new Dictionary("fin", "eng"));
        u.addDictionary("engfin", new Dictionary("eng", "fin"));

        System.out.println("FROM ON CREATE");
        u.getDictionary("engfin").addPair("hello", "moi");
        u.getDictionary("engfin").addPair("shop", "kauppa");

        //System.out.println(MyModelRoot.getInstance().getUserData().getDictionary("engfin"));
        /**Quiz engfinQuiz = new RandomQuiz(2,MyModelRoot.getInstance().getUserData().getDictionary("engfin"));
        doQuiz(engfinQuiz, u.getUser());
        System.out.println("GETTING TRACK RECORD");
        System.out.println(u.getUser().getTrackRecord());
        Quiz engfinHArdQuiz = new HardQuiz(1,u.getDictionary("engfin"),u.getUser().getTrackRecord() );
        doQuiz(engfinHArdQuiz, u.getUser());**/

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        System.out.println("FROM CREATE MENU");
        //add menu Item user with current user name
        menu.add(u.getUser().getUserName());
        menu.add("records");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //get the menu item user
        MenuItem item= menu.getItem(0);
        System.out.println("FROM ON PREPARE");
        System.out.println(item.getTitle());
        //set it as button
        item.setShowAsAction(2);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        ///if (id == R.id.action_settings) {
         //   return true;
        //}

        return super.onOptionsItemSelected(item);
    }
}
