package com.example.leeicheng.dogbook.mydog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.articles.Article;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaAction;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.owner.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.Inflater;


public class MyDogFragment extends Fragment {
    String TAG = "我的狗";
    RecyclerView rvMyDog;
    GridLayoutManager gridLayoutManager;
    private Uri croppedImageUri;
    static Bitmap photo;
    ImageView ivProfile, ivProfileBackground;
    ImageButton changeBackgroundPhoto;

    TextView tvProfileInfo;
    GeneralTask generalTask;
    Dog dog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mydog_fragment, container, false);
        Log.d(TAG, Common.getPreferenceAll(getActivity()).toString());
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
        int req = -1;
        if (action.equals(Common.PROFILE_PHOTO)) {
            req = Common.REQ_CROP_PROFILE_PICTURE;
        } else if (action.equals(Common.BACKGROUND_PHOTO)) {
            req = Common.REQ_CROP_BACKGROUND_PICTURE;
        }

        try {
            Intent intent = MediaAction.crop(getActivity(), srcImageUri, action);
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
        List<Article> myArticles;
        private Context context;
        Activity activity;

        public MyDogAdapter(Context context) {
            this.context = context;
            activity = (Activity) context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            myArticles = getMyArticles();

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
                int dogId = Common.getPreferencesDogId(getActivity());
                if (Common.getPreferencesIsLogin(context)) {
                    //登入後
                    if ((dog = CommonRemote.getDogInfo(dogId, getActivity())) != null) {
                        tvProfileInfo.setText(dog.getName());
                    }
                    CommonRemote.getProfilePhoto(dogId, ivProfile, getActivity());
                    dogMainViewHolder.getBackgroundPhoto(ivProfileBackground);
                    dogMainViewHolder.viewControlLogined();
                } else {
                    //登入前
                    dogMainViewHolder.viewsControlUnLogin();
                }

            } else {
                if (Common.getPreferencesIsLogin(context)) {
                    ArticlesViewHolder articlesViewHolder = (ArticlesViewHolder) holder;
                    Article article = myArticles.get(position - 1);
                    CommonRemote.getMedia(article.getMediaId(), articlesViewHolder.ivArticle, getActivity());

                    articlesViewHolder.ivArticle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(context, "這是 = " + position, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            if (myArticles != null) {
                return myArticles.size() + 1;
            } else {
                return 1;
            }
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
                changeBackgroundPhoto = itemView.findViewById(R.id.ibBackground);

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
                        } else {
                            int dogId = Common.getPreferencesDogId(getActivity());
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                            View dialogView = getLayoutInflater().inflate(R.layout.mydog_dialog, null);
                            final ImageView background = dialogView.findViewById(R.id.ivBackgroundDialog);
                            ImageView ProfilePhoto = dialogView.findViewById(R.id.ivProfilePhotoDialog);
                            TextView tvName = dialogView.findViewById(R.id.tvNameDialog);
                            TextView tvInfo = dialogView.findViewById(R.id.tvInfoDialog);
                            Button changeProfilePhoto = dialogView.findViewById(R.id.changeProfilePhoto);
                            Button signOut = dialogView.findViewById(R.id.signOut);
                            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity());

                            getBackgroundPhoto(background);
                            CommonRemote.getProfilePhoto(dogId, ProfilePhoto, getActivity());
                            tvName.setText(dog.getName());
                            String info = "品種：" + dog.getVariety() + "\n"
                                    + "性別：" + dog.getGender() + "\n"
                                    + "年紀：" + dog.getAge() + "\n"
                                    +  "生日：" + dateFormat.format(dog.getBirthday());

                            tvInfo.setText(info);

                            dialogBuilder.setView(dialogView);
                            final AlertDialog myDogDialog = dialogBuilder.create();
                            myDogDialog.show();

                            changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    choosePicture(Common.REQ_CHOOSE_PROFILE_PICTURE);
                                    myDogDialog.cancel();
                                }
                            });

                            signOut.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Common.setPreferenceClear(context);
                                    Common.disconnectServer();
                                    viewControl();
                                    myDogDialog.cancel();

                                }
                            });
                        }
                    }
                });

                changeBackgroundPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choosePicture(Common.REQ_CHOOSE_BACKGROUND_PICTURE);
                    }
                });
            }

            void getBackgroundPhoto(ImageView imageView) {
                int photoSize = context.getResources().getDisplayMetrics().widthPixels;
                int dogId = Common.getPreferencesDogId(context);
                if (Common.isNetworkConnect(context)) {
                    String url = Common.URL + "/MediaServlet";
                    mediaTask = new MediaTask(url, dogId, photoSize, imageView, Common.GET_PROFILE_BACKGROUND_PHOTO, "dog");
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

        List<Article> getMyArticles() {
            List<Article> articles = null;
            int dogId = Common.getPreferencesDogId(getActivity());
            if (Common.isNetworkConnect(getActivity())) {
                String url = Common.URL + "/ArticleServlet";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", Common.GET_MY_ARTICLES);
                jsonObject.addProperty("dogId", dogId);
                generalTask = new GeneralTask(url, jsonObject.toString());

                try {
                    String jsonIn = generalTask.execute().get();
                    Type type = new TypeToken<List<Article>>() {
                    }.getType();
                    articles = new Gson().fromJson(jsonIn, type);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
            return articles;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        viewControl();
    }
}
