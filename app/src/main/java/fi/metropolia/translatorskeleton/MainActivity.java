package fi.metropolia.translatorskeleton;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import fi.metropolia.translatorskeleton.model.HardQuiz;
import fi.metropolia.translatorskeleton.model.Quiz;
import fi.metropolia.translatorskeleton.model.QuizItem;
import fi.metropolia.translatorskeleton.model.RandomQuiz;
import fi.metropolia.translatorskeleton.model.TimeOutQuestion;
import fi.metropolia.translatorskeleton.model.TrackRecord;
import fi.metropolia.translatorskeleton.model.UserData;
import fi.metropolia.translatorskeleton.model.MyModelRoot;
import fi.metropolia.translatorskeleton.model.User;
import fi.metropolia.translatorskeleton.model.Dictionary;

public class MainActivity extends AppCompatActivity {
    private static int TIMEOUT = 5000;//five secs timeout
    private static boolean timeIsOut;


    private Quiz currQuiz;
    private User user;
    private UserData u = MyModelRoot.getInstance().getUserData();
    private TextView questionTv;
    private List<String> Trans_values;
    private int itemCounter = 0;
    private JSONArray dataArray;
    private JSONObject data;
    private AssetsPropertyReader assetsPropertyReader;
    private Context context;
    private Properties p;
    private Dictionary dictEngFin;
    private Dictionary dictFinEng;
    private String DBPath;


    public void parse(JSONObject json ) throws JSONException{
        Iterator<String> keys = json.keys();
        while(keys.hasNext()){
            String key = keys.next();
            String val = null;
            val = json.getString(key);
            Trans_values  = Arrays.asList(val.split(" "));
            for(int i =0; i < Trans_values.size(); i++){
                if(!Trans_values.get(i).contains("$")){
                        dictEngFin.addPair(key, Trans_values.get(i).trim());
                        dictFinEng.addPair(Trans_values.get(i).trim(),key);
                }
            }
        }
    }

    private class initDicFromMongoDBTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
    }

    private class saveDicToMongoDBTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return saveContent(params[0]);
            } catch (Exception e) {
                return "Unable to retrieve data. URL may be invalid.";
            }
        }
    }
    //addWordPair;
  public void addWordPair(View view){
        String fromWord;
        String toWord;
        TextView keyTv =(TextView)findViewById(R.id.key);
        fromWord = String.valueOf(keyTv.getText());
        TextView valTv =(TextView)findViewById(R.id.value);
        toWord = String.valueOf(valTv.getText());
      if (fromWord.matches("[a-zA-Z ]+")) {
            if (toWord.matches("[\\p{L} ]+")) {
                dictEngFin.addPair(fromWord, toWord);
                dictFinEng.addPair(toWord, fromWord);
            } else {
                System.out.println("Invalid input");
            }
        } else {
            System.out.println("Invalid input.");
        }
      System.out.println("FROM ADD PAIR");
      System.out.println(dictEngFin.getKeys());
    }

    public void deleteWordPair(View view){
        //System.out.print(dictEngFin.getLang1() + ": ");
        String keyWord;
        String keyVal;
        TextView keyTv =(TextView)findViewById(R.id.deleteKey);
        keyWord = String.valueOf(keyTv.getText());
        TextView valTv =(TextView)findViewById(R.id.deleteKeyVal);
        keyVal = String.valueOf(valTv.getText());
        if (keyWord.matches("[a-zA-Z ]+")) {
            if (keyVal.matches("[\\p{L} ]+")) {
                dictEngFin.deletePair(keyWord, keyVal);
                dictFinEng.deletePair(keyVal, keyWord);
            } else {
                System.out.println("Invalid input");
            }
        } else {
            System.out.println("Invalid input.");
        }
        System.out.println("FROM DELETE");
        System.out.println(dictEngFin.getKeys());

    }


    //Save user;
    //getUser;

    private String saveContent(String myurl) throws IOException {
        Dictionary currDic = dictEngFin;
        currDic.addPair("to look", "hae");
        URL url = new URL(myurl);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setRequestProperty("User-Agent", "GYUserAgentAndroid");
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");
        httpUrlConnection.setUseCaches(false);
        JSONObject tmpDic =  currDic.JsonObj();
        try {
            tmpDic.put("_id",data.get("_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataOutputStream outputStream = new DataOutputStream(httpUrlConnection.getOutputStream());
        outputStream.writeBytes(tmpDic.toString());
        outputStream.flush();
        outputStream.close();

        InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        responseStreamReader.close();
        String response = stringBuilder.toString();
        if(!response.isEmpty()){
             new initDicFromMongoDBTask().execute(DBPath);
        }
        return null;
    }

    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            System.out.println("RESPONSE IS " + response);
            is = conn.getInputStream();
            // Convert the InputStream into a string
            convertInputStreamToJson(is, length);
        } catch (JSONException e) {
            //e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return "good content has downloaded";
    }

    public String convertInputStreamToJson(InputStream stream, int length) throws IOException, UnsupportedEncodingException, JSONException {
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        dataArray = new JSONArray(responseStrBuilder.toString());
        data = (JSONObject) dataArray.get(0);
        parse(data);
        return null;
    }

    public void askQuestion (){
        if(itemCounter <= currQuiz.getQuizLength()) {
                System.out.println("");
                QuizItem item = currQuiz.getItem(itemCounter);
                String question = (itemCounter + 1) + ". " + item.getQuestion() + "?";
                questionTv = (TextView) findViewById(R.id.question);
                questionTv.setText(question);
        }else{


        }
    }

    public void finishQuiz(int numOfCorrectAnswers){
        itemCounter = 0;
        user.addQuiz(currQuiz);
        System.out.println("FINISHED QUIZ");
        //TrackRecord tr= new TrackRecord();
        System.out.println(user.getTrackRecord());

         //System.out.println(TrackRecord.getTotals().put(dictEngFin,));



        System.out.println(user.getUserName());
        System.out.println(user);
        Context context = getApplicationContext();

        CharSequence text = "Finished the quiz.\n "+
                            "with " +numOfCorrectAnswers ;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        //show original fragment
        //view list with scores
    }

    public void verifyAnswer(View view){
        EditText answerET = (EditText)findViewById(R.id.answer);
        String answer = answerET.getText().toString();
        int correctAnswerCounter = 0;
        if (currQuiz.checkAnswer(itemCounter, answer)) {
            correctAnswerCounter++;
            System.out.println("Correct answer!");
        }else{
            CharSequence text = "You did not get it right this time.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            System.out.println("You did not get it right this time.");
        }
        itemCounter++;
        if(itemCounter < currQuiz.getQuizLength()){
            askQuestion();
        }else{
            finishQuiz(correctAnswerCounter);
        }
    }

    public void doQuiz(View view){
        if(itemCounter == 0){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.listcontainer, new TakeQuizFragment());
            ft.commit();
            //makes transaction sync
            fm.executePendingTransactions();
            //set current quiz
            System.out.println("DO QUIZ");
            System.out.println(user.getTrackRecord().getWordCount());
            if(view.getTag().equals("1") || user.getTrackRecord().toString() == ""){
                currQuiz = new RandomQuiz(dictEngFin.getKeys().size(), dictEngFin);

            }else{
                currQuiz =new HardQuiz(5,dictEngFin,user.getTrackRecord());
            }
            askQuestion();
        }
        else{
            CharSequence text = "first finish the ongoing quiz first";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    public void changeToEditFragment(View view){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.listcontainer, new EditDicFragment());
        ft.commit();
    }

    public void callGoToDefaultScreen(View view){
        goToDefaultScreen();
    }
    public void goToDefaultScreen(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.listcontainer, new ChooseQuizFragment());
        ft.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        assetsPropertyReader = new AssetsPropertyReader(context);
        p = assetsPropertyReader.getProperties("app.properties");
        DBPath = p.get("DBPath").toString();
        //getContent

        new initDicFromMongoDBTask().execute(DBPath);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.listcontainer, new ChooseQuizFragment());
        ft.commit();

        u.setUser(new User("Myself"));
        this.user =  u.getUser();
        u.addDictionary("FinEng", new Dictionary("fin", "eng"));
        u.addDictionary("EngFin", new Dictionary("eng", "fin"));
        dictEngFin =u.getDictionary("EngFin");
        dictFinEng = u.getDictionary("FinEng");
        goToDefaultScreen();

    }

    public void saveDicToMongoDB(View view){
        System.out.println("From Save");
        new saveDicToMongoDBTask().execute(DBPath);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add menu Item user with current user name
        menu.add(u.getUser().getUserName());
        menu.add("backToMain");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //get the menu item user
        MenuItem item= menu.getItem(0);
        //set it as button
        item.setShowAsAction(2);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getTitle() == "backToMain"){
            goToDefaultScreen();
        }
        return super.onOptionsItemSelected(item);
    }
}
