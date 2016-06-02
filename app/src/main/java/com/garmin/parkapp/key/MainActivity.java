package com.garmin.parkapp.key;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pushwoosh.fragment.PushEventListener;
import com.pushwoosh.fragment.PushFragment;

public class MainActivity extends AppCompatActivity implements PushEventListener {

    public static final String PHONE_NUMBER = "phone.number";
    public static final int REQUEST_CODE = 1234;
    private EditText phoneNumberEditText;
    private Button saveButton;

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            savePhoneNumber();
            saveButton.setEnabled(false);
        }
    };
    private View.OnClickListener testButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            initCall();
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            saveButton.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(saveButtonListener);

        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        phoneNumberEditText.setHint(getPhoneNumber());
        phoneNumberEditText.addTextChangedListener(textWatcher);

        findViewById(R.id.test_open_button).setOnClickListener(testButtonListener);

        PushFragment.init(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PushFragment.onNewIntent(this, intent);
    }

    @Override
    public void doOnUnregisteredError(String s) {

    }

    @Override
    public void doOnRegisteredError(String s) {

    }

    @Override
    public void doOnRegistered(String s) {

    }

    @Override
    public void doOnMessageReceive(String s) {
        initCall();
    }

    @Override
    public void doOnUnregistered(String s) {

    }

    private void initCall() {
        String phoneNumber = getPhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            if (checkForPermission()) {
                startActivity(intent);
            }
        }
    }

    private String getPhoneNumber() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(PHONE_NUMBER, "");
    }

    private void savePhoneNumber() {
        String phoneNumber = phoneNumberEditText.getText().toString();
        if (!TextUtils.isEmpty(phoneNumber)) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(PHONE_NUMBER, phoneNumber);
            editor.apply();
        }
    }

    private boolean checkForPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    REQUEST_CODE);
            return false;
        } else return true;
    }
}
