package org.perfit.fitness.dashboard;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import org.perfit.fitness.R;

import java.util.ArrayList;

class SlidingImage_Adapter extends PagerAdapter {

    private final ArrayList<DashboardModel> imageModelArrayList;
    private final LayoutInflater inflater;

    public SlidingImage_Adapter(Context context, ArrayList<DashboardModel> imageModelArrayList) {
        this.imageModelArrayList = imageModelArrayList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return imageModelArrayList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.sliding_images, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout
                .findViewById(R.id.sliding_image_placeholder);
        imageView.setImageResource(imageModelArrayList.get(position).getImage_placeholders());
        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
