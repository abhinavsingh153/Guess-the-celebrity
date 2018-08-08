package com.abhinavsingh153.project.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String>  celebUrls = new ArrayList<>();

    ArrayList<String>  celebNames = new ArrayList<String>();

    // ChosenCeleb will store the randomNumber

    int chosenCeleb = 0;

    ImageView celebImage ;

    int locationOfCorrectAnswer = 0;

    String[] answers = new String[4];

    Button button0 ;
    Button button1 ;
    Button button2 ;
    Button button3 ;

    public void ChosenCeleb (View view){

        if (view.getTag().equals(Integer.toString(locationOfCorrectAnswer))){

            Toast.makeText(this,"Correct!" , Toast.LENGTH_SHORT).show();

        }

        else{
            Toast.makeText(this,"Incorrect!, Correct answer is" + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }

        nextQuestion();

    }

    public class ImageLoader extends AsyncTask<String, Void ,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url= new URL(urls[0]) ;

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.connect();

                InputStream inputStream =httpURLConnection.getInputStream();

                Bitmap celebBitmapIage = BitmapFactory.decodeStream(inputStream);

                return celebBitmapIage;

            }

            catch (Exception e)
            {
                e.printStackTrace();

            }

             return null;
        }
    }

    public class DownloadTask extends AsyncTask<String , Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            String htmlString="";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try {

                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream =httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while (data !=  -1)
                {
                    char current =(char) data;

                    htmlString += current;

                    data = inputStreamReader.read();

                }
                return  htmlString;



            } catch (MalformedURLException e) {

                e.printStackTrace();


            } catch (IOException e) {

                e.printStackTrace();


            }

            return  null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = findViewById(R.id.celebrity1);
        button1 = findViewById(R.id.celebrity2);
        button2 = findViewById(R.id.celebrity3);
        button3 = findViewById(R.id.celebrity4);

        celebImage = findViewById(R.id.celebrityImage);

        DownloadTask task = new DownloadTask();
        String htmlString = null;
        try {
            htmlString = task.execute("http://www.posh24.se/kandisar").get();

            //Splitting the whole html string or url content till the point we need the url content.

            String[] splitHtmlString = htmlString.split("<div class=\"sidebarContainer\">");

            // Using regex or pattern match for gettin gthe celebrity img address and name
            //celebriry img

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(htmlString);

            while (m.find()) {
                // System.out.println(m.group(1));
                //ADding the list of img addresses to the arrayList celebUrls
                celebUrls.add(m.group(1));

            }

            // celebrity names

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitHtmlString[0]);

            while (m.find()) {
                //ADding the list of img addresses to the arrayList celebNmaes
                celebNames.add(m.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Generating random number to sownlaod the image of acelbrity depending upon the chosen number.


        //  Log.i("HTML content" , htmlString );



       nextQuestion();

      //  Log.i("HTML content" , htmlString )
    }

    public void nextQuestion (){

        Random rand = new Random();
        chosenCeleb = rand.nextInt(celebUrls.size());

        ImageLoader imageLoader = new ImageLoader();

        Bitmap celebrityBitmap ;

        try {

            celebrityBitmap = imageLoader.execute(celebUrls.get(chosenCeleb)).get();

            celebImage.setImageBitmap(celebrityBitmap);

            locationOfCorrectAnswer = rand.nextInt(4);

            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    //populating one of the options with the correct answer
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    // populating the options with wrong answers.
                    incorrectAnswerLocation = rand.nextInt(celebUrls.size());

                    while (incorrectAnswerLocation == chosenCeleb) {
                        incorrectAnswerLocation = rand.nextInt(celebUrls.size());
                    }

                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }

        catch (Exception e){

            e.printStackTrace();
        }


    }
}
