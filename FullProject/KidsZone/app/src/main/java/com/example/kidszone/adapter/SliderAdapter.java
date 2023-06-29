package com.example.kidszone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.kidszone.R;

public class SliderAdapter extends PagerAdapter {
    public TextView privacyPopup;
    public int[] slide_images = {
            R.drawable.intro_icon1,
            R.drawable.intro_icon2,
            R.drawable.intro_icon_privacy_policy
    };
    public String[] slide_headings = {
            "Welcome to Kids' Zone",
            "Simple Blocker",
            "Privacy Policy Agreement"
    };
    public String[] slide_description = {
            "A simple way to protect your children from the big world of technology.",
            "Allowing children to use specific apps on the mobile.",
            "By continuing, you agree to our privacy policy."
    };
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;

    }

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
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImage = view.findViewById(R.id.slide_img);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_description);
        privacyPopup = view.findViewById(R.id.privacy_policy_link_popup);


        slideImage.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_description[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void hidePrivacyPopup() {
        privacyPopup.setVisibility(View.INVISIBLE);
    }


    public void showPrivacyPopup() {
        privacyPopup.setVisibility(View.VISIBLE);
    }
}
