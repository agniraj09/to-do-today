package com.arc.agni.todotoday.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arc.agni.todotoday.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public class SliderItemFragment extends Fragment {

    private static final String ARG_POSITION = "slider-position";
    // prepare all title ids arrays
    @StringRes
    private static final int[] PAGE_TITLES =
            new int[]{R.string.title1, R.string.title2, R.string.title3, R.string.title4};

    // prepare all subtitle ids arrays
    @StringRes
    private static final int[] PAGE_TEXT =
            new int[]{
                    R.string.title1_text, R.string.title2_text, R.string.title3_text, R.string.title4_text
            };

    // prepare all subtitle images arrays
    @StringRes
    private static final int[] PAGE_IMAGE =
            new int[]{
                    R.drawable.ic_slide1, R.drawable.ic_slide2, R.drawable.ic_slide3, R.drawable.ic_slide4
            };

    // prepare all background images arrays
    @StringRes
    private static final int[] BG_IMAGE = new int[]{
            R.color.low, R.color.medium, R.color.high, R.color.green
    };
    private int position;

    public SliderItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     *
     * @return A new instance of fragment SliderItemFragment.
     */
    public static SliderItemFragment newInstance(int position) {
        SliderItemFragment fragment = new SliderItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_slider_item, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // set page background
        view.setBackgroundColor(requireActivity().getResources().getColor(BG_IMAGE[position]));
        TextView title = view.findViewById(R.id.textView);
        TextView titleText = view.findViewById(R.id.textView2);
        ImageView imageView = view.findViewById(R.id.imageView);
        // set page title
        title.setText(PAGE_TITLES[position]);
        // set page sub title text
        titleText.setText(PAGE_TEXT[position]);
        // set page image
        imageView.setImageResource(PAGE_IMAGE[position]);
    }
}
