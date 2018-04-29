package com.example.leeicheng.dogbook.mydog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
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
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
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
    Dog dog;
    int ownerId, age;
    int getPhotoId = 0;
    String name, gender, variety, birthday;
    private Uri contentUri, croppedImageUri;

    private static final String PROFILE_PHOTO = "profilePhoto";
    private static final String BACKGROUND = "background";
    final String TAKE_PICTURE = "Take Picture";
    final String CHOOSE_PICTURE = "Choose Picture";

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
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                popupMenu.inflate(R.menu.add_dog_photo);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals(TAKE_PICTURE)) {
                            takePicture();
                        } else if (menuItem.getTitle().equals(CHOOSE_PICTURE)) {
                            choosePicture(Common.REQ_CHOOSE_PROFILE_PICTURE);
                        }
                        return true;
                    }
                });
                popupMenu.show();
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
                    dog = new Dog(ownerId, name, birthdayToDate, age, gender, variety);
                    sendDogInfo(dog);
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

    void show() {
        Log.d(TAG, "狗的ID = " + Common.getPreferencesDogId(this));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.REQ_TAKE_PICTURE:
                    if (contentUri != null) {
                        crop(contentUri,PROFILE_PHOTO);
                    }
                    break;
                case Common.REQ_CHOOSE_PROFILE_PICTURE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        crop(uri,PROFILE_PHOTO);
                    }
                    break;
                case Common.REQ_CROP_PROFILE_PICTURE:
                    try {
                        croppedImageUri = data.getData();
                        photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));
                        ivProfilePhoto.setImageBitmap(photo);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void takePicture() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "photo.jpg");
        contentUri = FileProvider.getUriForFile(this,getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        if (isIntentAvailable(intent)) {
            startActivityForResult(intent, Common.REQ_TAKE_PICTURE);
        }
    }

    public void choosePicture(int action) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (isIntentAvailable(intent)) {
            if (action == Common.REQ_CHOOSE_PROFILE_PICTURE) {
                startActivityForResult(intent, Common.REQ_CHOOSE_PROFILE_PICTURE);
            } else if (action == Common.REQ_CHOOSE_BACKGROUND_PICTURE) {
                startActivityForResult(intent, Common.REQ_CHOOSE_BACKGROUND_PICTURE);
            }
        }
    }

    public boolean isIntentAvailable(Intent intent) {
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent
                , packageManager.MATCH_DEFAULT_ONLY);
        return resolveInfos.size() > 0;
    }

    public void crop(Uri srcImageUri, String action) {
        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "photo_crop.jpg");

        int width = 0;
        int height = 0;
        int req = -1;

        if (action.equals(PROFILE_PHOTO)) {
            width = 150;
            height = 150;
            req = Common.REQ_CROP_PROFILE_PICTURE;

        } else if (action.equals(BACKGROUND)) {
            width = 160;
            height = 90;
            req = Common.REQ_CROP_BACKGROUND_PICTURE;
        }

        croppedImageUri = Uri.fromFile(file);
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(srcImageUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", width);
            intent.putExtra("aspectY", height);
            intent.putExtra("outputX", width);
            intent.putExtra("outputY", height);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedImageUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, req);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
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
            String url = Common.URL + "/DogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", "insert");
            jsonObject.addProperty("dog", gson.toJson(dog));

            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                String JsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(JsonIn, JsonObject.class);
                Common.setPreferencesDogId(this, jsonObject.get("dogId").getAsInt());

            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
