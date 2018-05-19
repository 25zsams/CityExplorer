package com.a25zsa.firebasetest.BottomSheetComponent;

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

/**
 * /**
 * Layout that allows the user to swipe left and right through "pages" of content
 */
public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private String[] imageUrls;

    /**
     * Instantiates a new View pager adapter.
     *
     * @param context   the context
     * @param imageUrls the image urls
     */
    public ViewPagerAdapter(Context context, LinkedList<String> imageUrls){
        this.context = context;
        this.imageUrls = listToArray(imageUrls);
    }

    /**
     * Instantiates a new View pager adapter.
     *
     * @param context   the context
     * @param imageUrls the image urls
     */
    public ViewPagerAdapter(Context context, String[] imageUrls){
        this.context = context;
        this.imageUrls = imageUrls;
    }

    /**
     * List to array string [ ].
     *
     * @param imageUrls the image urls
     * @return the string [ ]
     */
    public String[] listToArray(LinkedList<String> imageUrls){
        String[] temp = new String[imageUrls.size()];
        for(int i = 0; i < imageUrls.size(); i++){
            temp[i] = imageUrls.get(i);
        }
        return temp;
    }

    /**
     *
     * @return Return the number of views available.
     */
    @Override
    public int getCount() {
        return imageUrls.length;
    }

    /**
     * Determines whether a page View is associated with a specific key object as returned by instantiateItem
     * @param view Page View to check for association with object
     * @param object Object to check for association with view
     * @return true if view is associated with the key object object
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Create the page for the given position.
     * @param container The containing View in which the page will be shown.
     * @param position The page position to be instantiated.
     * @return Returns an Object representing the new page
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Picasso.get().load(imageUrls[position]).into(imageView);
        container.addView(imageView);
        return imageView;
    }

    /**
     * Remove a page for the given position.
     * @param container The containing View from which the page will be removed.
     * @param position The page position to be removed
     * @param object The same object that was returned by instantiateItem
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
