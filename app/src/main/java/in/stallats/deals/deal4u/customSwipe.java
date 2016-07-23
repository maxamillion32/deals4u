package in.stallats.deals.deal4u;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by User on 31-May-16.
 */
public class customSwipe extends PagerAdapter {
    private int[] pageIDsArray;
    private int count;
    private LayoutInflater layoutInflater;
    private int [] pageIDs = {R.drawable.desert, R.drawable.jellyfish, R.drawable.koala, R.drawable.penguins, R.drawable.tulips};

    public customSwipe(final ViewPager pager) {
        super();
        int actualNoOfIDs = pageIDs.length;
        count = actualNoOfIDs + 2;
        pageIDsArray = new int[count];
        for (int i = 0; i < actualNoOfIDs; i++) {
            pageIDsArray[i + 1] = pageIDs[i];
        }
        pageIDsArray[0] = pageIDs[actualNoOfIDs - 1];
        pageIDsArray[count - 1] = pageIDs[0];

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int pageCount = getCount();
                if (position == 0){
                    pager.setCurrentItem(pageCount-2,false);
                } else if (position == pageCount-1){
                    pager.setCurrentItem(1,false);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO Auto-generated method stub
            }
        });
    }

    public int getCount() {
        return count;
    }

    public float getPageWidth(int position) {
        return(1f);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public Object instantiateItem(View container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int pageId = pageIDsArray[position];
        View view = layoutInflater.inflate(R.layout.activity_custom_swipe, (ViewGroup) container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.swipe_image_view);
        imageView.setImageResource(pageId);

        ((ViewPager) container).addView(view, 0);
        return view;

        //layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View itemView = layoutInflater.inflate(R.layout.activity_custom_swipe, (ViewGroup) container, false);
        //ImageView imageView = (ImageView) itemView.findViewById(R.id.swipe_image_view);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        //imageView.setImageResource(pageIDsArray[position]);
        //((ViewPager) container).addView(itemView);
        //notifyDataSetChanged();
        //return itemView;

    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(View container) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        // TODO Auto-generated method stub
    }

    @Override
    public Parcelable saveState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void startUpdate(View container) {
        // TODO Auto-generated method stub
    }
}
