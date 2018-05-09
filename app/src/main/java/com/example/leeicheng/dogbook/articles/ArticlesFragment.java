package com.example.leeicheng.dogbook.articles;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ArticlesFragment extends Fragment {
    RecyclerView rvArticles;
    List<Article> articles;
    GeneralTask generalTask;
    MediaTask mediaTask;
    String TAG = "";
    Dog dog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.articles_fragment, container, false);
        findViews(view);
        articles = getArticle();
        return view;
    }

    void findViews(View view) {
        rvArticles = view.findViewById(R.id.rvArticles);
        rvArticles.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvArticles.setAdapter(new ArticlesAdapter(getActivity()));
    }


    public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {
        private Context context;

        ArticlesAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.article_item, parent, false);
            return new ArticleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ArticleViewHolder holder, int position) {
            Article article = articles.get(position);
            if ((dog = getDogInfo(article.getDogId())) != null) {
                holder.tvPosterName.setText(dog.getName());
            }
            if (article != null) {
                holder.tvArticleContent.setText(article.getContent());
                getProfilePhoto(article.getDogId(), holder.civProfilePhoto);
                getMedia(article.getMediaId(), holder.ivArticlePhoto);
            }
        }

        @Override
        public int getItemCount() {
            return articles.size();
        }

        public class ArticleViewHolder extends RecyclerView.ViewHolder {
            ImageView civProfilePhoto, ivLike, ivArticlePhoto;
            TextView tvPosterName, likesCount, tvArticleContent;

            public ArticleViewHolder(View view) {
                super(view);
                findViews(view);
            }

            void findViews(View view) {
                civProfilePhoto = view.findViewById(R.id.civProfilePhoto);
                ivLike = view.findViewById(R.id.ivLike);
                ivArticlePhoto = view.findViewById(R.id.ivArticlePhoto);
                tvArticleContent = view.findViewById(R.id.tvArticleContent);
                likesCount = view.findViewById(R.id.likesCount);
                tvPosterName = view.findViewById(R.id.tvPosterName);

            }
        }


    }

    Dog getDogInfo(int dogId) {
        Gson gson = new GsonBuilder().create();
        Dog dog = null;
        if (Common.isNetworkConnect(getActivity())) {
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return dog;
    }

    void getMedia(int MediaId, ImageView imageView) {
        int photoSize = getActivity().getResources().getDisplayMetrics().widthPixels;
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/MediaServlet";
            mediaTask = new MediaTask(url, MediaId, photoSize, imageView, Common.GET_ARTICLES, "media");
            try {
                mediaTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    void getProfilePhoto(int dogId, ImageView imageView) {
        int photoSize = getActivity().getResources().getDisplayMetrics().widthPixels;
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/MediaServlet";
            mediaTask = new MediaTask(url, dogId, photoSize, imageView, Common.GET_PROFILE_PHOTO, "dog");
            try {
                mediaTask.execute();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    List<Article> getArticle() {
        List<Article> articles = null;
        int dogId = Common.getPreferencesDogId(getActivity());
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.GET_ARTICLES);
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
