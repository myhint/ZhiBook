package com.srtianxia.zhibook.utils;

import com.srtianxia.zhibook.utils.cache.DoubleCache;

/**
 * Created by srtianxia on 2016/1/22.
 */
public class ImageLoader {

    private ImageLoader(){
        DoubleCache cache  = DoubleCache.builder();
    }

    public static ImageLoader builder(){
        return new ImageLoader();
    }


}

