package fi.metropolia.translatorskeleton;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Properties;

import fi.metropolia.translatorskeleton.model.HardQuiz;
import fi.metropolia.translatorskeleton.model.Quiz;
import fi.metropolia.translatorskeleton.model.QuizItem;
import fi.metropolia.translatorskeleton.model.RandomQuiz;
import fi.metropolia.translatorskeleton.model.TimeOutObserver;
import fi.metropolia.translatorskeleton.model.TimeOutQuestion;
import fi.metropolia.translatorskeleton.model.TrackRecord;
import fi.metropolia.translatorskeleton.model.UserData;
import fi.metropolia.translatorskeleton.model.MyModelRoot;
import fi.metropolia.translatorskeleton.model.User;
import fi.metropolia.translatorskeleton.model.Dictionary;

public class MainActivity extends AppCompatActivity implements TimeOutObserver {
    private static int TIMEOUT = 10000;//five secs timeout
    private static boolean timeIsOut;
    private Quiz currQuiz;
    private User user;
    private UserData u = MyModelRoot.getInstance().getUserData();
    private TextView questionTv;
    private List<String> Trans_values;
    private int itemCounter = 0;
    private JSONObject data;
    private JSONObject userData;
    private AssetsPropertyReader assetsPropertyReader;
    private Context context;
    private Properties p;
    private Dictionary dictEngFin;
    private Dictionary dictFinEng;
    private String DBPath;
    private String UsersDBPath;
    private TrackRecord tr = new TrackRecord();
    private int hardQuizCounter = 0;
    private int correctAnswerCounter;
    private Fragment mainActvFrg= new ChooseQuizFragment();
    private boolean initialized;
    Thread t;



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

    private String saveUserContent(String myurl) throws IOException, JSONException {
        JSONObject tmpUsr = new JSONObject();
        String falseWordsStr  ="";
        HashMap<String, Integer> map = tr.getTotals().get(tr.getTotals().keySet().toArray()[0]);
           Iterator mapItr = map.entrySet().iterator();
            while (mapItr.hasNext()) {
                Map.Entry pair = (Map.Entry)mapItr.next();
                System.out.println("HERE");
                System.out.println(pair.getKey() + " = " + pair.getValue());
                if(pair.getValue() == 1){
                    //System.out.println(pair.getKey());
                    falseWordsStr = falseWordsStr + pair.getKey() + " ";
                }
        }
        System.out.println(userData);
        tmpUsr = userData;
        System.out.println("FALSE WORDS");
        System.out.println("false words str: " + falseWordsStr);
        tmpUsr.put("falseWords",falseWordsStr);

        HttpURLConnection httpUrlConnection = setUpHttpReq(UsersDBPath, "POST");
        sendHttpReq(httpUrlConnection,tmpUsr);
        String response = getHttpRes(httpUrlConnection);
        if(!response.isEmpty()){
            new getUserFromMongoDBTask().execute(UsersDBPath);
        }
        return null;
    }

    @Override
    public void timeout() {
        timeIsOut = true;
    }

    private class getUserFromMongoDBTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response = new JSONObject();
            try {
                response = getUserHistory(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  response;
        }
    }

    private JSONObject getUserHistory(String username) throws IOException, JSONException {
        ArrayList<Integer> indexis= new ArrayList<>() ;
        Quiz currQuiz = new RandomQuiz(dictEngFin.getKeys().size(),dictEngFin);
        InputStream is = null;
        int length = 500;
        URL url = new URL(UsersDBPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        String response  = getHttpRes(conn);
        JSONArray dataArray = new JSONArray(response);
        userData = (JSONObject)dataArray.get(0);
        if (is != null) {
            is.close();
        }

        JSONObject tmp = (JSONObject) userData;
        String faultyWordsTmp = tmp.get("falseWords").toString();
        String[] faultywords = faultyWordsTmp.split(" ");
        for(int i= 0; i < currQuiz.getQuizLength(); i++){
            for(int z=0 ; z < faultywords.length; z++) {
                if (faultywords[z].equals(currQuiz.getItem(i).getQuestion())) {
                    indexis.add(i);
                }
            }
        }
        for(int i = 0; i < indexis.size(); i++){
            currQuiz.getItem(indexis.get(i)).setSolved(true);
        }
        tr.add(currQuiz);
        return null;
    }

    private class initDicFromMongoDBTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "Unable to retrieve data. URL may be invalid.";
        }
    }

    private class saveUserToMongoDBTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                System.out.println("FROM ASYNC SAVE USER");
                return saveUserContent(params[0]);
            } catch (Exception e) {
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
    //getUser;
    public HttpURLConnection setUpHttpReq(String dbpath, String httpverb) throws IOException  {
        URL url = new URL(dbpath);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setReadTimeout(10000);
        httpUrlConnection.setConnectTimeout(15000);
        httpUrlConnection.setRequestMethod(httpverb);
        httpUrlConnection.setRequestProperty("User-Agent", "GYUserAgentAndroid");
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");
        httpUrlConnection.setUseCaches(false);
        return httpUrlConnection;
    }

    public void sendHttpReq(HttpURLConnection connection, JSONObject objToWrite) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(objToWrite.toString());
        outputStream.flush();
        outputStream.close();
    }

    public String getHttpRes(HttpURLConnection connection) throws IOException {
        InputStream responseStream = new BufferedInputStream(connection.getInputStream());
        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
        String line = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        responseStreamReader.close();
        String response = stringBuilder.toString();
        System.out.println("from res");
        System.out.println(response);
        return response;
    }

    private String saveContent(String myurl) throws IOException, JSONException {
        Dictionary currDic = dictEngFin;
        JSONObject tmpDic =  currDic.JsonObj();
        tmpDic.put("_id", data.get("_id"));
        HttpURLConnection httpUrlConnection = setUpHttpReq(myurl, "POST");
        sendHttpReq(httpUrlConnection,tmpDic);
        String response = getHttpRes(httpUrlConnection);
        if(!response.isEmpty()){
             new initDicFromMongoDBTask().execute(DBPath);
        }
        return null;
    }

    private String downloadContent(String myurl) throws IOException, JSONException {
        InputStream is = null;
        int length = 500;
        URL url = new URL(myurl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        String response  = getHttpRes(conn);
        JSONArray dataArray = new JSONArray(response);
        data = (JSONObject)dataArray.get(0);
        parse(data);
        return "good content has downloaded";
    }



    public void askQuestion (){
        if(itemCounter <= currQuiz.getQuizLength()) {
            QuizItem item = currQuiz.getItem(itemCounter);
            String question = (itemCounter + 1) + ". " + item.getQuestion() + "?";
            questionTv = (TextView) findViewById(R.id.question);
            questionTv.setText(question);

            TimeOutQuestion toq = new TimeOutQuestion(TIMEOUT);
            toq.registerTimeOutObserver(this);
            t = new Thread(toq);
            t.start();
            timeIsOut = false;


        }

    }

    public void finishQuiz(int numOfCorrectAnswers) throws IOException, JSONException {
        tr.add(currQuiz);

        new saveUserToMongoDBTask().execute("some");
        Context context = getApplicationContext();
        CharSequence text = "Finished the quiz.\n "+
                            "with " +numOfCorrectAnswers ;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        replaceFragment(mainActvFrg);
        itemCounter = 0;
    }

    public void goToNextQuestion(View view) throws IOException, JSONException {
        itemCounter++;
        if(itemCounter < currQuiz.getItems().size()){

            askQuestion();
        }else{
            finishQuiz(correctAnswerCounter);
        }
    }

    public void verifyAnswer(View view) throws IOException, JSONException, InterruptedException {
        t.interrupt();
        EditText answerET = (EditText)findViewById(R.id.answer);
        String answer = answerET.getText().toString();
        if(!timeIsOut){
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
                if(timeIsOut){

                }
                askQuestion();
            }else{
                finishQuiz(correctAnswerCounter);
            }

        }else if(timeIsOut){
            CharSequence text = "timed out click next Question.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            System.out.println("VERIFY ANSWER TIME IS OUT");
        }

    }


    public void replaceFragment(Fragment frg){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.listcontainer, frg);
        ft.commit();
        fm.executePendingTransactions();
    }

    public void doQuiz(View view){
         correctAnswerCounter = 0;
        Fragment frg = new TakeQuizFragment();
        if(itemCounter == 0){

            if(view.getTag().equals("1") || tr.getWordCount() == 0){
                currQuiz = new RandomQuiz(dictEngFin.getKeys().size(), dictEngFin);
                replaceFragment(frg);
                askQuestion();

            }else if(!tr.getNegatives(dictEngFin).isEmpty()){
                currQuiz =new HardQuiz(5,dictEngFin,tr);
                hardQuizCounter++;
                replaceFragment(frg);
                askQuestion();
            }else {
                System.out.println("YOU HAVE COMPLETED THE DICTIONARY");
                //OUTPUT SOME MESSAGE OR RESET DICTIONARY
            }
        }
        else{
            CharSequence text = "first finish the ongoing quiz first";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            replaceFragment(frg);
            askQuestion();
        }
    }

    public void changeToEditFragment(View view){
        Fragment edtFrg= new EditDicFragment();
        replaceFragment(edtFrg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        assetsPropertyReader = new AssetsPropertyReader(context);
        p = assetsPropertyReader.getProperties("app.properties");
        DBPath = p.get("DBPath").toString();
        UsersDBPath = p.get("UsersDBPath").toString();
        new initDicFromMongoDBTask().execute(DBPath);
        replaceFragment(mainActvFrg);

        u.setUser(new User("Myself"));
        this.user =  u.getUser();
        new getUserFromMongoDBTask().execute(user.getUserName());
        user.setTrackRecord(tr);
        u.addDictionary("FinEng", new Dictionary("fin", "eng"));
        u.addDictionary("EngFin", new Dictionary("eng", "fin"));
        dictEngFin =u.getDictionary("EngFin");
        dictFinEng = u.getDictionary("FinEng");


    }

    public void saveDicToMongoDB(View view){
        new saveDicToMongoDBTask().execute(DBPath);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(u.getUser().getUserName());
        menu.add("backToMain");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item= menu.getItem(0);
        item.setShowAsAction(2);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getTitle() == "backToMain"){
            replaceFragment(mainActvFrg);
        }
        return super.onOptionsItemSelected(item);
    }
}
