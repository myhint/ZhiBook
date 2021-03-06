package com.srtianxia.zhibook.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.srtianxia.zhibook.app.API;
import com.srtianxia.zhibook.app.APP;
import com.srtianxia.zhibook.model.Imodel.IZhiBookModel;
import com.srtianxia.zhibook.model.bean.zhibook.Answer;
import com.srtianxia.zhibook.model.bean.zhibook.AnswerBean;
import com.srtianxia.zhibook.model.bean.zhibook.CollectFolderBean;
import com.srtianxia.zhibook.model.bean.zhibook.EssayBean;
import com.srtianxia.zhibook.model.bean.zhibook.Note;
import com.srtianxia.zhibook.model.bean.zhibook.QuestionBean;
import com.srtianxia.zhibook.model.callback.OnCollectListener;
import com.srtianxia.zhibook.model.callback.OnGetAnswerListener;
import com.srtianxia.zhibook.model.callback.OnGetCollectListener;
import com.srtianxia.zhibook.model.callback.OnGetNoteListener;
import com.srtianxia.zhibook.model.callback.OnGetQuestionListener;
import com.srtianxia.zhibook.model.callback.OnPraiseListener;
import com.srtianxia.zhibook.model.callback.OnSaveListener;
import com.srtianxia.zhibook.model.callback.OnSetAnswerListener;
import com.srtianxia.zhibook.model.callback.OnSetQuestionListener;
import com.srtianxia.zhibook.model.callback.OnUpLoadPicLisener;
import com.srtianxia.zhibook.model.callback.OnUploadListener;
import com.srtianxia.zhibook.utils.SharedPreferenceUtils;
import com.srtianxia.zhibook.utils.TimeUtils;
import com.srtianxia.zhibook.utils.db.DataBaseHelper;
import com.srtianxia.zhibook.utils.http.HttpUtils;
import com.srtianxia.zhibook.utils.http.OkHttpUtils;
import com.srtianxia.zhibook.utils.http.RetrofitAPI;
import com.srtianxia.zhibook.utils.http.callback.OkHttpUtilsCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
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
    private OkHttpClient client;

    private ZhiBookModel(){
        File cacheFile = new File(APP.getContext().getExternalCacheDir(),"ZhiBookCache");
        Cache cache = new Cache(cacheFile,1024*1024*50);
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!HttpUtils.isNetworkConnected(APP.getContext())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                if (HttpUtils.isNetworkConnected(APP.getContext())) {
                    int maxAge = 0 * 60;
                    // 有网络时 设置缓存超时时间0个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                } else {
                    // 无网络时，设置超时为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
        client = new OkHttpClient.Builder().cache(cache)
                .addInterceptor(interceptor)
                .build();
        retrofit = new Retrofit.Builder().
                baseUrl(RetrofitAPI.BASIC_URL).
                addConverterFactory(GsonConverterFactory.create()).
                addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                client(client).
                build();
        retrofitAPI = retrofit.create(RetrofitAPI.class);
    }

    public static ZhiBookModel getInstance(){
        return zhiBookModel;
    }

    @Override
    public void setQuestion(String title, String content, String token, final OnSetQuestionListener listener) {
        final Handler handler = new Handler();
        OkHttpUtils.asyPost(API.setQuestion, new OkHttpUtilsCallback() {
            @Override
            public void onResponse(final Response response, String status) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(response.code() == 200){
                            listener.success();
                        }else {
                            listener.failure();
                        }
                    }
                });
            }
        },new OkHttpUtils.Param("title",title)
         ,new OkHttpUtils.Param("content",content)
         ,new OkHttpUtils.Param("token",SharedPreferenceUtils.getToken()));
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
    public void setAnswer(String content, String questionId, String token, final OnSetAnswerListener listener) {
        final Handler handler = new Handler();
        OkHttpUtils.asyPost(API.setAnswer, new OkHttpUtilsCallback() {
            @Override
            public void onResponse(final Response response, String status) throws IOException {
                Log.d(TAG,response.body().string());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response.code() == 200){
                            listener.success();
                        }else {
                            listener.failure();
                        }
                    }
                });
            }
        },new OkHttpUtils.Param("questionId",questionId)
         ,new OkHttpUtils.Param("content",content)
         ,new OkHttpUtils.Param("token",SharedPreferenceUtils.getToken()));
    }

    @Override
    public void getAnswer(String questionId, final OnGetAnswerListener listener) {
        Log.d(TAG,"questionId = "+questionId);
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
                        Log.d(TAG,"answer.size = "+answerBean.getAnswers().size());
                        for (Answer a:answerBean.getAnswers()){
                            Log.d(TAG,a.getContent());
                        }
                    }
                });
    }

    @Override
    public void praise(int i, final OnPraiseListener listener) {
        final Handler handler = new Handler();
        OkHttpUtils.asyPost(API.addPraise, new OkHttpUtilsCallback() {
            @Override
            public void onResponse(Response response, String status) throws IOException {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.add();
                    }
                });
            }
        },new OkHttpUtils.Param("id",String.valueOf(i)));
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

    /**
     * 考虑这里存储应该在io线程，回调告知保存成功
     * @param content
     * @param authorId
     * @param listener
     */

    @Override
    public void saveNoteToDB(final String content, final Integer authorId, final OnSaveListener listener) {
        Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                DataBaseHelper dataBaseHelper = new DataBaseHelper(APP.getContext(),
                        "zhibook.db",null,1);
                SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("content",content);
                values.put("authorId",authorId);
                values.put("date", TimeUtils.getTime());
                subscriber.onNext(db.insert("note",null,values));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onNext(Long l) {
                        if (l!=-1){
                            listener.success();
                        }else {
                            listener.failure();
                        }
                    }
                });

//        DataBaseHelper dataBaseHelper = new DataBaseHelper(APP.getContext(),
//                "zhibook.db",null,1);
//        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("content",content);
//        values.put("authorId",authorId);
//        if (db.insert("note",null,values)!= -1){
//            values.clear();
//            listener.success();
//        }else {
//            values.clear();
//            listener.failure();
//        }
    }

    /**
     * rx
     * @param authorId
     * @param listener
     */
    @Override
    public void getNoteList(final Integer authorId, final OnGetNoteListener listener) {
        Observable.create(new Observable.OnSubscribe<List<Note>>() {
            @Override
            public void call(Subscriber<? super List<Note>> subscriber) {
                List<Note> notes = new ArrayList<>();
                DataBaseHelper dataBaseHelper = new DataBaseHelper(APP.getContext(),
                        "zhibook.db",null,1);
                SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM note WHERE authorId = "+ authorId,null);
                if (cursor.moveToFirst()){
                    do {
                        notes.add(new Note(
                                cursor.getString(cursor.getColumnIndex("content")),
                                cursor.getInt(cursor.getColumnIndex("authorId")),
                                cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("date"))));
                    }while (cursor.moveToNext());
                }
                subscriber.onNext(notes);
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<Note>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG,e.getMessage());
            }

            @Override
            public void onNext(List<Note> notes) {
                listener.success(notes);
            }
        });

//        List<Note> notes = new ArrayList<>();
//        DataBaseHelper dataBaseHelper = new DataBaseHelper(APP.getContext(),
//                "zhibook.db",null,1);
//        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM note WHERE authorId = "+ authorId,null);
//        if (cursor.moveToFirst()){
//            do {
//                notes.add(new Note(
//                        cursor.getString(cursor.getColumnIndex("content")),
//                        cursor.getInt(cursor.getColumnIndex("authorId")),
//                        cursor.getInt(cursor.getColumnIndex("id"))));
//            }while (cursor.moveToNext());
//        }
//        cursor.close();
//        if (notes.size()!=0){
//            listener.success(notes);
//        }else {
//            listener.failure();
//        }
    }

    @Override
    public void upLoadHead(Uri uri, String token, final OnUploadListener listener) {
        token = SharedPreferenceUtils.getToken();
        final String finalToken = token;
        final Handler handler = new Handler();
        BTPFileResponse response = BmobProFile.getInstance(APP.getContext()).upload(uri.getPath(), new UploadListener() {
            @Override
            public void onSuccess(final String fileName, String url, final BmobFile file) {
                Log.i("bmob","文件上传成功："+fileName+",可访问的文件地址："+file.getUrl());
                OkHttpUtils.asyPost(API.updatePersonInfo, new OkHttpUtilsCallback() {
                    @Override
                    public void onResponse(final Response response, String status) throws IOException {
                        SharedPreferenceUtils.changeHead(file.getUrl());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (response.code() == 200){
                                    listener.success(file.getUrl());
                                }else if (response.code() == 400){
                                    listener.failure();
                                }
                            }
                        });
                    }
                },new OkHttpUtils.Param("token", finalToken), new OkHttpUtils.Param("headurl",file.getUrl()));
            }

            @Override
            public void onProgress(int progress) {
                Log.i("bmob","onProgress :"+progress);
                listener.progress(progress);
            }

            @Override
            public void onError(int statuscode, String errormsg) {
                Log.i("bmob","文件上传失败："+errormsg);
            }
        });
    }

    @Override
    public void upLoadPic(final Uri uri, final OnUpLoadPicLisener listener) {
        BTPFileResponse response = BmobProFile.getInstance(APP.getContext()).upload(uri.getPath(), new UploadListener() {
            @Override
            public void onSuccess(final String fileName, String url, final BmobFile file) {
                Log.i("bmob","文件上传成功："+fileName+",可访问的文件地址："+file.getUrl());
                listener.success(uri,file.getUrl());
            }

            @Override
            public void onProgress(int progress) {
                Log.i("bmob","onProgress :"+progress);

            }

            @Override
            public void onError(int statuscode, String errormsg) {
                Log.i("bmob","文件上传失败："+errormsg);
            }
        });
    }
}
