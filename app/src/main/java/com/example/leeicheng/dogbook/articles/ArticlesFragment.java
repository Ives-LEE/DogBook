package com.example.leeicheng.dogbook.articles;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.CommonRemote;
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
    ArticlesAdapter articlesAdapter;
    Dog dog;
    //....
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.articles_fragment, container, false);
        findViews(view);
        articles = getArticles();
        return view;
    }

    void findViews(View view) {
        rvArticles = view.findViewById(R.id.rvArticles);
        viewsControl();
    }

    void viewsControl() {
        rvArticles.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        articlesAdapter = new ArticlesAdapter();
        rvArticles.setAdapter(articlesAdapter);
    }


    public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder> {

        @Override
        public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.article_item, parent, false);
            return new ArticleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ArticleViewHolder holder, final int position) {
            final Article article = articles.get(position);
            Drawable drawable;
            int likeCount;
            if ((dog = CommonRemote.getDogInfo(article.getDogId(), getActivity())) != null) {
                holder.tvPosterName.setText(dog.getName());
            }
            if (article != null) {
                holder.tvArticleContent.setText(article.getContent());
                CommonRemote.getProfilePhoto(article.getDogId(), holder.civProfilePhoto, getActivity());
                CommonRemote.getMedia(article.getMediaId(), holder.ivArticlePhoto, getActivity());
            }

            holder.cbLike.setOnCheckedChangeListener(null);

            if (isLike(article.getArticleId())) {
                drawable = getResources().getDrawable(R.drawable.ic_favorite_24dp);
                holder.cbLike.setBackground(drawable);
                holder.cbLike.setChecked(true);
            } else {
                drawable = getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
                holder.cbLike.setBackground(drawable);
                holder.cbLike.setChecked(false);
            }


            holder.cbLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                Drawable drawable;
                int articleId = article.getArticleId();

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean selected) {
                    if (selected) {
                        addLike(articleId);
                        drawable = getResources().getDrawable(R.drawable.ic_favorite_24dp);
                        holder.cbLike.setBackground(drawable);
                        holder.cbLike.setChecked(selected);
                    } else {
                        deleteLike(articleId);
                        drawable = getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
                        holder.cbLike.setBackground(drawable);
                        holder.cbLike.setChecked(selected);
                    }
                }
            });

            likeCount = getLikeCount(article.getArticleId());
            holder.likesCount.setText(likeCount + " likes");

        }

        @Override
        public int getItemCount() {
            return articles.size();
        }

        public class ArticleViewHolder extends RecyclerView.ViewHolder {
            ImageView civProfilePhoto, ivArticlePhoto;
            CheckBox cbLike;
            TextView tvPosterName, likesCount, tvArticleContent;

            public ArticleViewHolder(View view) {
                super(view);
                findViews(view);
            }

            void findViews(View view) {
                civProfilePhoto = view.findViewById(R.id.civProfilePhoto);
                cbLike = view.findViewById(R.id.cbLike);
                ivArticlePhoto = view.findViewById(R.id.ivArticlePhoto);
                tvArticleContent = view.findViewById(R.id.tvArticleContent);
                likesCount = view.findViewById(R.id.likesCount);
                tvPosterName = view.findViewById(R.id.tvPosterName);
            }
        }

    }

    List<Article> getArticles() {
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

    boolean isLike(int articleId) {
        boolean isLike = false;
        int dogId = Common.getPreferencesDogId(getActivity());
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.SELECT_LIKE);
            jsonObject.addProperty("articleId", articleId);
            jsonObject.addProperty("dogId", dogId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(jsonIn, JsonObject.class);
                isLike = jsonObject.get("isLike").getAsBoolean();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return isLike;
    }

    void addLike(int articleId) {
        int dogId = Common.getPreferencesDogId(getActivity());
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.ADD_LIKE);
            jsonObject.addProperty("articleId", articleId);
            jsonObject.addProperty("dogId", dogId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                generalTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    void deleteLike(int articleId) {
        int dogId = Common.getPreferencesDogId(getActivity());
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.DELETE_LIKE);
            jsonObject.addProperty("articleId", articleId);
            jsonObject.addProperty("dogId", dogId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                generalTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }

    }

    int getLikeCount(int articleId) {
        int likeCount = 0;
        if (Common.isNetworkConnect(getActivity())) {
            String url = Common.URL + "/ArticleServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("status", Common.GET_LIKE_COUNT);
            jsonObject.addProperty("articleId", articleId);

            generalTask = new GeneralTask(url, jsonObject.toString());

            try {
                String jsonIn = generalTask.execute().get();
                jsonObject = new Gson().fromJson(jsonIn, JsonObject.class);
                likeCount = jsonObject.get("likeCount").getAsInt();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return likeCount;
    }

    @Override
    public void onResume() {
        super.onResume();
        articles = getArticles();
        articlesAdapter.notifyDataSetChanged();
    }
}
