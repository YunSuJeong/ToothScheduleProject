package com.example.toothscheduleproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends Activity implements View.OnClickListener {

    private FirebaseAuth mFirebaseAuth;     // 파이어베이스 인증
    private EditText mEtEmail, mEtPwd;      // 로그인 입력필드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mFirebaseAuth = FirebaseAuth.getInstance(); //FirebaseAuth 객체의 공유 인스턴스를 가져옴

        mEtEmail = findViewById(R.id.edtID);
        mEtPwd = findViewById(R.id.edtPW);

        findViewById(R.id.btnNoLogin).setOnClickListener(this);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnSignUp).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNoLogin:
                // 비회원 시작, 비회원일 경우 설문조사는 하지 않음
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 파괴
                break;

            case R.id.btnLogin:
                // 로그인 요청
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                if(strEmail.equals("")||strPwd.equals("")){
                    Toast.makeText(Login.this, "아이디 또는 비빌번호를 입력", Toast.LENGTH_SHORT).show();
                }
                else {

                    mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // 로그인 성공, 로그인하면 설문조사 페이지로 넘어감
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // 현재 액티비티 파괴
                            } else {
                                Log.e("LOGIN_ERROR", task.getException().getMessage());
                                Toast.makeText(Login.this, "로그인 실패..!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;

            case R.id.btnSignUp:
                // 회원가입 화면으로 이동
                Intent signUpIntent = new Intent(Login.this, SignUp.class);
                startActivity(signUpIntent);
                break;
        }
    }
}
