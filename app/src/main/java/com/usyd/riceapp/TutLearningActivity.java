package com.usyd.riceapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TutLearningActivity extends BaseActivity {


    private ListView lv1;
    private EditText E1;

    private int total_number;

    private int[] imagesId={R.mipmap.what_is_leaf_blast,
            R.mipmap.lesson_of_disease,
            R.mipmap.bacterial_leaf,
            R.mipmap.major_disease,
            R.mipmap.brown_spot,
            R.mipmap.paddy_crop,
            R.mipmap.rice_blast_diseases};

    private	String[] names;
    private  String[] contents={"25:47","16:33","13:05","5:12","17:56","12:51","5:31"};
    private String[] video_url={
            "https://www.youtube.com/watch?v=KAlvzK_391s",
            "https://www.youtube.com/watch?v=4V7icIKeaSA",
            "https://www.youtube.com/watch?v=QvgQMmy9HzI",
            "https://www.youtube.com/watch?v=p8MGjRjhxrM",
            "https://www.youtube.com/watch?v=PR_eMPrDZ_k&t=454s",
            "https://www.youtube.com/watch?v=LqaMUu6gecs",
            "https://www.youtube.com/watch?v=oiWWdBaVVKU"
    };


    private  String [] Search_video_url={"https://www.youtube.com/watch?v=KAlvzK_391s",
            "https://www.youtube.com/watch?v=4V7icIKeaSA",
            "https://www.youtube.com/watch?v=QvgQMmy9HzI",
            "https://www.youtube.com/watch?v=p8MGjRjhxrM",
            "https://www.youtube.com/watch?v=PR_eMPrDZ_k&t=454s",
            "https://www.youtube.com/watch?v=LqaMUu6gecs",
            "https://www.youtube.com/watch?v=oiWWdBaVVKU"
    };

    private String[] Search_name=new String[7];
    private int[] Search_image=new int[7];
    private String [] Search_content=new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_new_tut, null, false);
        this.drawer.addView(contentView, 0);
        names = new String[] {getResources().getString(R.string.test1_txt),
                getResources().getString(R.string.test2_txt),
                getResources().getString(R.string.test3_txt),
                getResources().getString(R.string.test4_txt),
                getResources().getString(R.string.test5_txt),
                getResources().getString(R.string.test6_txt),
                getResources().getString(R.string.test7_txt)};
        setPagename(TutLearningActivity.this, "Tutorial Learning");

        lv1 = (ListView) findViewById(R.id.listView1);
        E1 = (EditText) findViewById(R.id.Ed_search);

        E1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {


                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // hide inout window
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);





                    String words=E1.getText().toString();
                    int mmm=0;

                    for(int i=0;i<names.length;i++) {
                        Pattern p = Pattern.compile(words.toLowerCase());
                        Matcher m = p.matcher(names[i].toLowerCase());

                        if (m.find()) {
                            Search_name[mmm] = names[i];
                            Search_image[mmm] = imagesId[i];
                            Search_content[mmm] = contents[i];
                            Search_video_url[mmm]=video_url[i];
                            mmm++;
                        }
                    }

                    total_number=mmm;

                    BaseAdapter adapter2 = new BaseAdapter() {

                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View layout=View.inflate(TutLearningActivity.this, R.layout.tut_style, null);
                            ImageView face = (ImageView)layout.findViewById(R.id.face);
                            TextView name =(TextView)layout.findViewById(R.id.name);
                            TextView mark = (TextView)layout.findViewById(R.id.mark);

                            face.setImageResource(Search_image[position]);
                            name.setText(Search_name[position]);
                            mark.setText(Search_content[position]);

                            return layout;
                        }

                        @Override
                        public long getItemId(int position) {

                            return position;
                        }

                        @Override
                        public Object getItem(int position) {

                            return Search_name[position];
                        }

                        @Override
                        public int getCount() {

                            return total_number;
                        }


                    };///new BaseAdapter()

                    lv1.setAdapter(adapter2);



                }
                return false;
            }
        });




        BaseAdapter adapter = new BaseAdapter() {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout=View.inflate(TutLearningActivity.this, R.layout.tut_style, null);
                ImageView face = (ImageView)layout.findViewById(R.id.face);
                TextView name =(TextView)layout.findViewById(R.id.name);
                TextView mark = (TextView)layout.findViewById(R.id.mark);

                face.setImageResource(imagesId[position]);
                name.setText(names[position]);
                mark.setText(contents[position]);

                return layout;
            }

            @Override
            public long getItemId(int position) {

                return position;
            }

            @Override
            public Object getItem(int position) {

                return names[position];
            }

            @Override
            public int getCount() {

                return names.length;
            }


        };///new BaseAdapter()

        lv1.setAdapter(adapter);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Uri uri = Uri.parse(Search_video_url[0]);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }

                if(position==1){
                    Uri uri = Uri.parse(Search_video_url[1]);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }

                if(position==2){
                    Uri uri = Uri.parse(Search_video_url[2]);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }

                if(position==3){
                    Uri uri = Uri.parse(Search_video_url[3]);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }

                if(position==4){
                    Uri uri = Uri.parse(Search_video_url[4]);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }

                if(position==5){
                    Uri uri = Uri.parse(Search_video_url[5]);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }

                if(position==6){
                    Uri uri = Uri.parse(Search_video_url[6]);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                }


            }
        });
    }


}
