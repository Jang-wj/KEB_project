package com.example.project_dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AddActivity extends AppCompatActivity {

    EditText etWord;
    EditText etMeaning;
    Button btOK, btSearch;
    String getresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etWord = findViewById(R.id.etWord);
        etMeaning = findViewById(R.id.etMeaning);
        btOK = findViewById(R.id.btOk);
        btSearch = findViewById(R.id.btSearch);

        Intent i = getIntent();
        String word = i.getStringExtra("word");
        String meaning = i.getStringExtra("meaning");

        etWord.setText(word);
        etMeaning.setText(meaning);



        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Translater translater = new Translater();
                translater.execute();
            }
        });

        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ir = getIntent() ;
                String word = etWord.getText().toString();
                String meaning = etMeaning.getText().toString();
                ir.putExtra("word" , word) ;
                ir.putExtra("meaning" , meaning) ;
                setResult(RESULT_OK , ir);
                finish();
            }
        });
    }

    class Translater extends AsyncTask<String ,Void, String > {
        @Override
        protected String doInBackground(String... strings) {

            //////네이버 API

            String clientId = BuildConfig.clientId;
            String clientSecret = BuildConfig.clientSecret;

            try {
                String text = URLEncoder.encode(etWord.getText().toString(), "UTF-8");

                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("X-Naver-Client-Id", clientId);
                connection.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source=en&target=ko&text=" + text;
                connection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = connection.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                }
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println(response.toString());
                getresult = response.toString();

                getresult = getresult.split("\"")[15];
                etMeaning.setText(getresult);

            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

    }
}