package com.example.leeicheng.dogbook;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddDogActivity extends AppCompatActivity {
    String TAG = "新增狗";
    ImageView ivProfilePhoto;
    EditText etName, etBirthday, etAge;
    RadioGroup rgGender;
    Spinner sVariety;
    Button btnAdd, btnCancel;
    Bitmap photo;
    GeneralTask generalTask;
    Common common;
    Dog dog;
    int ownerId, age;
    int getPhotoId = 0;
    String name, gender, variety ,birthday;
    private Uri contentUri, croppedImageUri;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_CROP_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dog_activity);
        findViews();
    }

    void findViews() {
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        etName = findViewById(R.id.etNameAddDog);
        etBirthday = findViewById(R.id.etBirthdayAddDog);
        etAge = findViewById(R.id.etAgeAddDog);
        rgGender = findViewById(R.id.rgGenderAddDog);
        sVariety = findViewById(R.id.sVarietyAddDog);
        btnAdd = findViewById(R.id.btnAddDog);
        btnCancel = findViewById(R.id.btnCancelAddDog);

        viewsControl();
    }

    void viewsControl() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");



        ivProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        etBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                showDatePicker();

            }
        });

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int item) {
                hideKeyboard();
                int select = rgGender.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(select);
                gender = radioButton.getText().toString();
            }
        });

        setVarietySipnner();
        sVariety.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //TODO
                hideKeyboard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //TODO
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                birthday = etBirthday.getText().toString();
                age = Integer.valueOf(etAge.getText().toString());
                variety = "god";
                ownerId = Common.getPreferencesOwnerId(getApplicationContext());
                try {
                    Date birthdayToDate = format.parse(birthday);
                    dog = new Dog(ownerId, name, birthdayToDate, age, gender,variety );
                    sendDogInfo(dog);
                    show();
                    sendMedia();
                    finish();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    void show(){
        Log.d(TAG,"狗的ID = "+Common.getPreferencesDogId(this));
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    //設定Sipnner的item
    void setVarietySipnner() {
        String[] variety = {"Plz chose variety", "黃金獵犬", "柯基"};
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, variety);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sVariety.setAdapter(spinnerAdapter);
        sVariety.setSelection(0, true);
    }

    //顯示月曆對話
    void showDatePicker() {
        int year, month, day;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                etBirthday.setText(year + "-" + month + "-" + day);
            }
        }, year, month, day).show();
    }

    void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "photo.jpg");
        contentUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (isIntentAvailable(this, intent)) {
            startActivityForResult(intent, REQ_TAKE_PICTURE);
        }
    }


    boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent
                , packageManager.MATCH_DEFAULT_ONLY);
        return resolveInfos.size() > 0;
    }

    void crop(Uri srcImageUri) {
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "photo_crop.jpg");
        croppedImageUri = Uri.fromFile(file);
        try {

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(srcImageUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 100);
            intent.putExtra("aspectY", 100);
            intent.putExtra("outputX", 0);
            intent.putExtra("outputY", 0);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQ_CROP_PICTURE);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;
                case REQ_CROP_PICTURE:
                    try {
                        photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));
//                        profilePhoto.setImageBitmap(photo);
                        ivProfilePhoto.setImageBitmap(photo);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    void sendMedia() {
        String TAG = "送";
        if (Common.isNetworkConnect(this)) {
            String url = Common.URL + "/MediaServlet";
            byte[] image = Common.bitmapToPNG(photo);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "insert");
            jsonObject.addProperty("dogId", Common.getPreferencesDogId(this));
            jsonObject.addProperty("media", Base64.encodeToString(image, Base64.DEFAULT));
            jsonObject.addProperty("type", 1);
            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                String jsonIn = generalTask.execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);
                getPhotoId = jObject.get("photoId").getAsInt();
                Log.d(TAG, "id = " + getPhotoId);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }


    void sendDogInfo(Dog dog) {
        if (Common.isNetworkConnect(this)) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String url = common.URL + "/DogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "insert");
            jsonObject.addProperty("dog", gson.toJson(dog));

            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                String JsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(JsonIn, JsonObject.class);
                Common.setPreferencesDogId(this,jsonObject.get("dogId").getAsInt());

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
