package com.srtianxia.zhibook.model;

import android.util.Log;

import com.srtianxia.zhibook.app.API;
import com.srtianxia.zhibook.model.Imodel.IZhiBookModel;
import com.srtianxia.zhibook.model.bean.zhibook.AnswerBean;
import com.srtianxia.zhibook.model.bean.zhibook.CollectFolderBean;
import com.srtianxia.zhibook.model.bean.zhibook.EssayBean;
import com.srtianxia.zhibook.model.bean.zhibook.QuestionBean;
import com.srtianxia.zhibook.model.callback.OnCollectListener;
import com.srtianxia.zhibook.model.callback.OnGetAnswerListener;
import com.srtianxia.zhibook.model.callback.OnGetCollectListener;
import com.srtianxia.zhibook.model.callback.OnGetQuestionListener;
import com.srtianxia.zhibook.model.callback.OnPraiseListener;
import com.srtianxia.zhibook.model.callback.OnSaveListener;
import com.srtianxia.zhibook.utils.http.OkHttpUtils;
import com.srtianxia.zhibook.utils.http.RetrofitAPI;
import com.srtianxia.zhibook.utils.http.callback.OkHttpUtilsCallback;

import java.io.IOException;

import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by srtianxia on 2016/2/11.
 */
public class ZhiBookModel implements IZhiBookModel {

    private static final String TAG = "ZhiBookModel";
    private static ZhiBookModel zhiBookModel = new ZhiBookModel();
    private Retrofit retrofit;
    private RetrofitAPI retrofitAPI;

    private ZhiBookModel(){
        retrofit = new Retrofit.Builder().
                baseUrl(RetrofitAPI.BASIC_URL).
                addConverterFactory(GsonConverterFactory.create()).
                addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public static ZhiBookModel getInstance(){
        return zhiBookModel;
    }

    @Override
    public void setQuestion(String title, String content, String token) {

    }

    @Override
    public void getQuestion(final OnGetQuestionListener listener) {
        retrofitAPI.getQuestion().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Observer<QuestionBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.failure(e.getMessage());
                    }

                    @Override
                    public void onNext(QuestionBean questionBean) {
                        listener.success(questionBean.getQuestions());
                    }
                });
    }

    @Override
    public void setAnswer() {

    }

    @Override
    public void getAnswer(String questionId, final OnGetAnswerListener listener) {
        retrofitAPI.getAnswer(questionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AnswerBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,e.getMessage());
                    }

                    @Override
                    public void onNext(AnswerBean answerBean) {
                        listener.success(answerBean.getAnswers());
                    }
                });
    }

    @Override
    public void praise(int i, OnPraiseListener listener) {
        //对点赞进行判断 ！！
        if (i == 1){
            listener.add();
        }else {
            listener.reduce();
        }
    }

    @Override
    public void getCollectionFolder(String token, final OnGetCollectListener listener) {
        retrofitAPI.getCollection(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CollectFolderBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,e.getMessage());
                    }

                    @Override
                    public void onNext(CollectFolderBean bean) {
                        listener.success(bean);
                    }
                });
    }

    @Override
    public void addCollectionFolder(String token, String folder, final OnCollectListener listener) {
//        retrofitAPI.addFolder(token,folder)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d(TAG,e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        Log.d(TAG,s);
//                    }
//                });
        OkHttpUtils.asyPost(API.addCollectFolder, new OkHttpUtilsCallback() {
            @Override
            public void onResponse(Response response, String status) throws IOException {
                listener.success();
            }
        },new OkHttpUtils.Param("token",token),new OkHttpUtils.Param("folder",folder));
    }

    @Override
    public void setCollect(String token, int answerId, OnCollectListener listener) {

    }

    @Override
    public void cancelCollect(String token, int answerId, OnCollectListener listener) {

    }

    @Override
    public void addNote(String token, String title, String content, String authorId, String isPrivate, OnSaveListener listener) {
        retrofitAPI.setEssay(title,content,token,isPrivate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<EssayBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(EssayBean essayBean) {

                    }
                });
    }


}
