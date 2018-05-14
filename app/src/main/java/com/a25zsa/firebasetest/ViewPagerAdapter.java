package com.a25zsa.firebasetest;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;

/**
 * Created by 25zsa on 5/12/2018.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private String[] imageUrls;

    public ViewPagerAdapter(Context context, LinkedList<String> imageUrls){
        this.context = context;
        this.imageUrls = listToArray(imageUrls);
    }

    public ViewPagerAdapter(Context context, String[] imageUrls){
        this.context = context;
        this.imageUrls = imageUrls;
    }

    public String[] listToArray(LinkedList<String> imageUrls){
        String[] temp = new String[imageUrls.size()];
        for(int i = 0; i < imageUrls.size(); i++){
            temp[i] = imageUrls.get(i);
        }
        return temp;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Picasso.get().load(imageUrls[position]).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
