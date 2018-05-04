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

import com.example.leeicheng.dogbook.R;
import com.example.leeicheng.dogbook.main.Common;
import com.example.leeicheng.dogbook.main.GeneralTask;
import com.example.leeicheng.dogbook.media.MediaTask;
import com.example.leeicheng.dogbook.mydog.Dog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ArticlesFragment extends Fragment {
    RecyclerView rvArticles;
    List<Article> articles;
    GeneralTask generalTask;
    MediaTask mediaTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.articles_fragment, container, false);
        findViews(view);
        articles = new ArrayList<>();
        return view;
    }

    void findViews(View view) {
        rvArticles = view.findViewById(R.id.rvArticles);
        rvArticles.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvArticles.setAdapter(new ArticlesAdapter(getActivity()));
//        viewsControl(view);
    }

    void viewsControl(View view) {

    }

    private class ArticlesAdapter extends RecyclerView.Adapter {
        private Context context;

        ArticlesAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.article_item, parent, false);
            return new ArticleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return articles.size();
        }

        private class ArticleViewHolder extends RecyclerView.ViewHolder {
            public ArticleViewHolder(View view) {
                super(view);
            }
        }

        void getArticle() {
            int dogId = Common.getPreferencesDogId(context);
            Gson gson = new GsonBuilder().create();
            if (Common.isNetworkConnect(context)) {
                String url = Common.URL + "/";
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("status", Common.GET_ARTICLES);
                jsonObject.addProperty("dogId",dogId);
                generalTask = new GeneralTask(url, jsonObject.toString());

                try {
                    String Articlesjson = generalTask.execute().get();
                    gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
    }


}
