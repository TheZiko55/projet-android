package com.example.project;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener {

    final int MSG_CALCUL = 1;

    List<String> Liste_Name = new ArrayList<String>();
    List<Integer> Liste_UID = new ArrayList<Integer>();
    List<String> Liste_RSS = new ArrayList<String>();
    List<Boolean> Liste_click = new ArrayList<Boolean>();

    Boolean pauser = false;
    int running = 0;
    private int bId;





    // Thread
    Runnable runn = new Runnable(){
        public void run() {
            if (!pauser) {
                getInformation();
                String messageString = "Mise à jour " + bId;
                Message msg = mHandler.obtainMessage(MSG_CALCUL, (Object) messageString);
                mHandler.sendMessage(msg);
                mHandler.postDelayed(runn, 5000); // delay of 5000 ms
            }
        }
    };


    // Handler
    @SuppressLint("HandlerLeak")
    final Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what==MSG_CALCUL & Liste_click.get(bId)) {
                Toast.makeText(getBaseContext(), "Info:" + (String) msg.obj, Toast.LENGTH_LONG).show();
                TextView tx = (TextView) findViewById(bId + 200);
                tx.setText("RSS:" + Liste_RSS.get(bId));
            }
            else if (!Liste_click.get(bId)){
                mHandler.removeCallbacks(runn);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        getInformation();
        LinearLayout lile = (LinearLayout) findViewById(R.id.linearlayout);
        for (int i = 0; i < running; ++i) {
            View v = createProcessView(Liste_Name.get(i), Liste_RSS.get(i),Liste_UID.get(i), i);
            Liste_click.add(false);
            lile.addView(v);
        }
    }


    @SuppressLint("ResourceType")
    public View createProcessView (String pack, String vm,int UID,  int id){
        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams topLeft = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        topLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        topLeft.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        RelativeLayout.LayoutParams topRight = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        topRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        topRight.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        RelativeLayout.LayoutParams bottom = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


        TextView someTextView = new TextView(this);
        TextView textView = new TextView(this);
        textView.setId(id + 200);
        Button boutton = new Button(this);
        boutton.setText("Monitor");
        boutton.setId(id);
        someTextView.setText("[" + UID + "]" + pack);
        someTextView.setId(999);
        bottom.addRule(RelativeLayout.BELOW, 999);
        textView.setText("RSS:" + vm);




        layout.addView(someTextView, topLeft);
        layout.addView(textView, bottom);
        boutton.setOnClickListener(this);
        layout.addView(boutton, topRight);

        return layout;
    }



    public void getInformation () {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List packAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        int UID = 0;
        String strPackageName = null;


        for (Object object : packAppsList) {
            ResolveInfo info = (ResolveInfo) object;
            strPackageName = info.activityInfo.applicationInfo.packageName.toString();
            UID = info.activityInfo.applicationInfo.uid;
            Log.d("App Name", strPackageName);
            Log.d("UID", String.valueOf(UID));
            Process process = null;

            try {
                process = new ProcessBuilder("ps").start();
            } catch (IOException e) {
                return;
            }

            InputStream in = process.getInputStream();
            Scanner scanner = new Scanner(in);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("u0_")) {
                    String[] temp = line.split(" ");
                    String pack = temp[temp.length - 1];
                    if (pack.equals(strPackageName)) {
                        running++;
                        String RSS = temp[4];
                        Log.d("RSS?", RSS);
                        Liste_Name.add(pack);
                        Liste_RSS.add(RSS);
                        Liste_UID.add(UID);
                    }

                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        bId = v.getId();
        Liste_click.set(bId,!Liste_click.get(bId));
        Log.d("bId", String.valueOf(bId));
        new Thread(runn).start();
    }

    //gestion d'interuption
    @Override
    protected void onResume() {
        super.onResume();
        getInformation();
    }
    @Override
    protected void onPause() {
        super.onPause();
        pauser = true;
    }
    
}