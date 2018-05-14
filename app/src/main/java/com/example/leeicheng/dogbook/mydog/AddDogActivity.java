package com.example.leeicheng.dogbook.mydog;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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

import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.media.MediaAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
                        if (menuItem.getItemId() == R.id.takePicture) {
                            takePicture();
                        } else if (menuItem.getItemId() == R.id.choosePicture) {
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
                variety = sVariety.getSelectedItem().toString();
                ownerId = Common.getPreferencesOwnerId(getApplicationContext());
                try {
                    Date birthdayToDate = format.parse(birthday);
                    dog = new Dog(ownerId, name, gender, variety, age, birthdayToDate);
                    sendDogInfo(dog);
                    CommonRemote.sendMedia(photo, getApplicationContext());
                    int dogId = Common.getPreferencesDogId(getApplicationContext());
                    if ( dogId != -1){
                        Common.connectServer(getApplicationContext(), dogId);
                    }
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
                        crop(contentUri, PROFILE_PHOTO);
                    }
                    break;
                case Common.REQ_CHOOSE_PROFILE_PICTURE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        crop(uri, PROFILE_PHOTO);
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

        contentUri = MediaAction.takePicture(this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);

        if (MediaAction.isIntentAvailable(intent, this)) {
            startActivityForResult(intent, Common.REQ_TAKE_PICTURE);
        }
    }

    public void choosePicture(int action) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (MediaAction.isIntentAvailable(intent, this)) {
            if (action == Common.REQ_CHOOSE_PROFILE_PICTURE) {
                startActivityForResult(intent, Common.REQ_CHOOSE_PROFILE_PICTURE);
            } else if (action == Common.REQ_CHOOSE_BACKGROUND_PICTURE) {
                startActivityForResult(intent, Common.REQ_CHOOSE_BACKGROUND_PICTURE);
            }
        }
    }


    public void crop(Uri srcImageUri, String action) {
        int req = -1;
        if (action.equals(PROFILE_PHOTO)) {
            req = Common.REQ_CROP_PROFILE_PICTURE;

        } else if (action.equals(BACKGROUND)) {
            req = Common.REQ_CROP_BACKGROUND_PICTURE;
        }
        try {
            Intent intent = MediaAction.crop(this, srcImageUri, action);
            startActivityForResult(intent, req);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    void sendDogInfo(Dog dog) {
        if (Common.isNetworkConnect(this)) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
            String url = Common.URL + "/DogServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.ADD_DOG);
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
