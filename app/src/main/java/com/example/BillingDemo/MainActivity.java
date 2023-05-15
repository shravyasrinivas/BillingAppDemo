package com.example.BillingDemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//FirebaseAuth auth;
//Button button;
//TextView textView;
//FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                Toast.makeText(getApplicationContext(), "Sales Page", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),uploaddata.class);
                startActivity(intent);
                finish();
                break;
            case R.id.button2:
                Toast.makeText(getApplicationContext(), "Loan Page", Toast.LENGTH_SHORT).show();
                Intent intent2=new Intent(getApplicationContext(),SalesPage.class);
                startActivity(intent2);
                finish();
                break;

        }
    }
}
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        auth=FirebaseAuth.getInstance();
//        button=findViewById(R.id.logout);
//        textView=findViewById(R.id.user_details);
//        user=auth.getCurrentUser();
//        if(user==null){
//            Intent intent=new Intent(getApplicationContext(),Login.class);
//            startActivity(intent);
//            finish();
//        }
//        else{
//            textView.setText(user.getEmail());
//        }
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent=new Intent(getApplicationContext(),Login.class);
//                startActivity(intent);
//                finish();
//
//            }
//        });
//    }
//}