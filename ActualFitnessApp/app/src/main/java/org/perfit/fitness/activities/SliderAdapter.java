package org.perfit.fitness.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import org.perfit.fitness.R;

class SliderAdapter extends PagerAdapter {

    private final Context mcontext;

    public SliderAdapter(Context context) {
        this.mcontext = context;
    }

    private final int[] slide_images = {
            R.drawable.yoga_class_8,
            R.drawable.yoga_class_1_person,
            R.drawable.yoga_class_4
    };

    private final String[] slide_headings= {
            "Workout Now",
            "Track Fitness",
            "Challenge Yourself"
    };

    private final String[] slide_decriptions = {
            "Smart Workout and Tracking",
            "Track your improvements",
            "Share your workout and performance online"
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageaView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_desciption);

        slideImageaView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_decriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }
}
