package com.example.leeicheng.dogbook.mydog;

import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.owner.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MyDogFragment extends Fragment {
    String TAG = "我的狗";
    RecyclerView rvMyDog;
    GridLayoutManager gridLayoutManager;
    private Uri croppedImageUri;
    static Bitmap photo;
    ImageView ivProfile, ivProfileBackground, ivCalendarToolbar;
    TextView tvProfileInfo, tvTitle;
    GeneralTask generalTask;
    Dog dog;
    Toolbar myDogToolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mydog_fragment, container, false);
        Log.d(TAG,Common.getPreferenceAll(getActivity()).toString());
        findViews(view);

        return view;
    }


    void findViews(View view) {
        rvMyDog = view.findViewById(R.id.rvMyDog);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        viewControl();
    }

    void viewControl() {
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //第一個位置
                if (position == 0) {
                    // return spanCount's 3/3
                    return 3;
                } else {
                    // return spanCount's 1/3
                    return 1;
                }
            }
        });
        rvMyDog.setLayoutManager(gridLayoutManager);
        rvMyDog.setAdapter(new MyDogAdapter(getActivity()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case Common.REQ_CHOOSE_PROFILE_PICTURE:
                    Uri profilePhotoUri = data.getData();
                    if (profilePhotoUri != null) {
                        crop(profilePhotoUri, Common.PROFILE_PHOTO);
                    }
                    break;
                case Common.REQ_CHOOSE_BACKGROUND_PICTURE:
                    Uri backgroundPhotoUri = data.getData();
                    if (backgroundPhotoUri != null) {
                        crop(backgroundPhotoUri, Common.BACKGROUND_PHOTO);
                    }
                    break;
                case Common.REQ_CROP_PROFILE_PICTURE:
                    try {
                        croppedImageUri = data.getData();
                        photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(croppedImageUri));
                        ivProfile.setImageBitmap(photo);
                        resetProfilePhoto();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case Common.REQ_CROP_BACKGROUND_PICTURE:
                    try {
                        croppedImageUri = data.getData();
                        photo = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(croppedImageUri));
                        ivProfileBackground.setImageBitmap(photo);
                        setBackgroundPhoto();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
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
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent
                , packageManager.MATCH_DEFAULT_ONLY);
        return resolveInfos.size() > 0;
    }

    public void crop(Uri srcImageUri, String action) {
        File file = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "photo_crop.jpg");

        int width = 0;
        int height = 0;
        int req = -1;

        if (action.equals(Common.PROFILE_PHOTO)) {
            width = 150;
            height = 150;
            req = Common.REQ_CROP_PROFILE_PICTURE;
        } else if (action.equals(Common.BACKGROUND_PHOTO)) {
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
            Toast.makeText(getActivity(), "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    void resetProfilePhoto() {
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/MediaServlet";
            byte[] image = Common.bitmapToPNG(photo);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.SET_PROFILE_PHOTO);
            jsonObject.addProperty("dogId", Common.getPreferencesDogId(getActivity()));
            jsonObject.addProperty("media", Base64.encodeToString(image, Base64.DEFAULT));
            jsonObject.addProperty("type", 1);
            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                String jsonIn = generalTask.execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);

                Log.d(TAG, "成功 = " + jObject.get("isSuccess").getAsString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }


    void setBackgroundPhoto() {
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/MediaServlet";
            byte[] image = Common.bitmapToPNG(photo);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.SET_PROFILE_BACKGROUND_PHOTO);
            jsonObject.addProperty("dogId", Common.getPreferencesDogId(getActivity()));
            jsonObject.addProperty("media", Base64.encodeToString(image, Base64.DEFAULT));
            jsonObject.addProperty("type", 1);
            generalTask = new GeneralTask(url, jsonObject.toString());
            try {
                String jsonIn = generalTask.execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn, JsonObject.class);

                Log.d(TAG, "成功 = " + jObject.get("isSuccess").getAsString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    // adapter 區
    public class MyDogAdapter extends RecyclerView.Adapter {

        MediaTask mediaTask;
        String TAG = "我的狗";
        int TYPE_ONE = 0;
        int TYPE_TWO = 1;
        final String DOG_INFO = "Dog Info";
        final String SIGN_OUT = "Sign Out";
        final String CHANGE_DOG_PROFILE_PHOTO = "Change Profile Photo";
        final String CHANGE_DOG_BACKGROUND_PHOTO = "Change Background Photo";

        private Context context;
        Activity activity;

        public MyDogAdapter(Context context) {
            this.context = context;
            activity = (Activity) context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            if (viewType == 0) {
                View itemView = layoutInflater.inflate(R.layout.mydog_main_item, parent, false);
                return new DogMainViewHolder(itemView);
            } else {
                View itemView = layoutInflater.inflate(R.layout.mydog_article_item, parent, false);
                return new ArticlesViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (position == 0) {
                DogMainViewHolder dogMainViewHolder = (DogMainViewHolder) holder;

                if (Common.getPreferencesIsLogin(context)) {
                    //登入後
                    if ((dog = dogMainViewHolder.getDogInfo()) != null){
                        tvProfileInfo.setText(dog.getName());
                    }
                    dogMainViewHolder.getProfilePhoto();
                    dogMainViewHolder.getBackgroundPhoto();
                    dogMainViewHolder.viewControlLogined();
                } else {
                    //登入前
                    dogMainViewHolder.viewsControlUnLogin();
                }

            } else {
                ArticlesViewHolder articlesViewHolder = (ArticlesViewHolder) holder;

                articlesViewHolder.ivArticle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "這是 = " + position, Toast.LENGTH_SHORT).show();

                    }
                });

            }
        }

        @Override
        public int getItemCount() {
            //TODO 文章列表 必 >= 1
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return TYPE_ONE;
                default:
                    return TYPE_TWO;
            }
        }

        //主功能
        public class DogMainViewHolder extends RecyclerView.ViewHolder {

            public DogMainViewHolder(View itemView) {
                super(itemView);
                ivProfile = itemView.findViewById(R.id.ivAddProfile);
                tvProfileInfo = itemView.findViewById(R.id.tvProfileInfo);
                ivProfileBackground = itemView.findViewById(R.id.ivProfileBackground);
            }

            void viewsControlUnLogin() {
                ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showLoginLayout();
                    }
                });
            }

            void viewControlLogined() {

                ivProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "已經登入了");
                        if (Common.getPreferencesDogId(context) == -1) {
                            Intent intent = new Intent(context, AddDogActivity.class);
                            context.startActivity(intent);
                        }
                    }
                });

                ivProfile.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        PopupMenu popupMenu = new PopupMenu(context, view);
                        popupMenu.inflate(R.menu.dog_info_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if (menuItem.getItemId() == R.id.info) {

                                } else if (menuItem.getItemId() == R.id.changeProfilePhoto) {
                                    choosePicture(Common.REQ_CHOOSE_PROFILE_PICTURE);

                                } else if (menuItem.getItemId() == R.id.changeBackgroundPhoto) {
                                    choosePicture(Common.REQ_CHOOSE_BACKGROUND_PICTURE);

                                } else if (menuItem.getItemId() == R.id.signOut) {
                                    Common.setPreferenceClear(context);
                                    tvProfileInfo.setText(Common.getPreferenceAll(context));
                                }
                                return true;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                });
            }

            Dog getDogInfo() {
                int dogId = Common.getPreferencesDogId(context);
                Gson gson = new GsonBuilder().create();
                Dog dog = null;
                if (Common.isNetworkConnect(context)) {
                    String url = Common.URL + "/DogServlet";
                    JsonObject jsonObject = new JsonObject();
                    dog = new Dog(dogId);

                    jsonObject.addProperty("status", Common.GET_DOG_INFO);
                    jsonObject.addProperty("dog", gson.toJson(dog));

                    generalTask = new GeneralTask(url, jsonObject.toString());

                    try {
                        String info = generalTask.execute().get();
                        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                        dog = gson.fromJson(info, Dog.class);
//                        Log.d("", "info = " + info + "dog = " + dog.toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                return dog;
            }

            void getProfilePhoto() {
                int photoSize = context.getResources().getDisplayMetrics().widthPixels / 4;
                int dogId = Common.getPreferencesDogId(context);
                if (Common.isNetworkConnect(context)) {
                    String url = Common.URL + "/MediaServlet";
                    mediaTask = new MediaTask(url, dogId, photoSize, ivProfile, Common.GET_PROFILE_PHOTO);
                    try {
                        mediaTask.execute();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }

            void getBackgroundPhoto() {
                int photoSize = context.getResources().getDisplayMetrics().widthPixels;
                int dogId = Common.getPreferencesDogId(context);
                if (Common.isNetworkConnect(context)) {
                    String url = Common.URL + "/MediaServlet";
                    mediaTask = new MediaTask(url, dogId, photoSize, ivProfileBackground, Common.GET_PROFILE_BACKGROUND_PHOTO);
                    try {
                        mediaTask.execute();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }


        }

        // 顯示文
        public class ArticlesViewHolder extends RecyclerView.ViewHolder {
            ImageView ivArticle;

            public ArticlesViewHolder(View itemView) {
                super(itemView);
                ivArticle = itemView.findViewById(R.id.ivArticleDogItem);
            }
        }

        void showLoginLayout() {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }
}
