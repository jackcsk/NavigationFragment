package com.dmcapps.navigationfragmentexample.NavigationFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dmcapps.navigationfragment.fragments.INavigationFragment;
import com.dmcapps.navigationfragmentexample.R;
import com.dmcapps.navigationfragment.fragments.NavigationFragment;
import com.dmcapps.navigationfragmentexample.TestIntentLaunchingActivity;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SampleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SampleFragment extends NavigationFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FRAG_TEXT = "ARG_FRAG_TEXT";
    private static final String ARG_FRAG_COUNT = "ARG_FRAG_COUNT";

    private String mFragText;
    private SampleModel model;

    private EditText edit1;

    private int mFragCount;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SampleFragment.
     */
    public static SampleFragment newInstance(String param1, int fragCount) {
        SampleFragment fragment = new SampleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAG_TEXT, param1);
        args.putInt(ARG_FRAG_COUNT, fragCount);
        fragment.setArguments(args);
        return fragment;
    }

    public SampleFragment() {
        // Required empty public constructor
        model = new SampleModel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFragText = getArguments().getString(ARG_FRAG_TEXT);
            mFragCount = getArguments().getInt(ARG_FRAG_COUNT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        setTitle("Sample Fragment " + mFragCount);
        setMasterToggleTitle("Master");

        // Using this to test if the memory space of the activity changes on rotation in the child
        Activity activity = getActivity();
        if (activity != null) {
            // Debug into this to check mHost is changed.
            setHasOptionsMenu(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sample, container, false);
        ((TextView) view.findViewById(R.id.sample_tv_text)).setText((mFragCount + 1) + " " + mFragText);

        edit1 = (EditText) view.findViewById(R.id.sample_et_text_1);
        edit1.setText(model.text1);
        edit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.text1 = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText edit2 = (EditText) view.findViewById(R.id.sample_et_text_2);
        edit2.setText(model.text2);
        edit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.text2 = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EditText edit3 = (EditText) view.findViewById(R.id.sample_et_text_3);
        edit3.setText(model.text3);
        edit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.text3 = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        view.findViewById(R.id.sample_btn_present).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                INavigationFragment fragmentToPresent = SampleFragment.newInstance("Fragment added to Stack.", (mFragCount + 1));
                presentFragment(fragmentToPresent);
            }
        });

        view.findViewById(R.id.sample_btn_present_override_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                INavigationFragment fragmentToPresent = SampleFragment.newInstance("Fragment added to Stack.", (mFragCount + 1));

                overrideNextAnimation(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
                presentFragment(fragmentToPresent);
            }
        });

        view.findViewById(R.id.sample_btn_present_bundle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                INavigationFragment fragmentToPresent = SampleFragment.newInstance("Fragment added to Stack.", (mFragCount + 1));

                overrideNextAnimation(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
                presentFragment(fragmentToPresent, new Bundle());
            }
        });

        view.findViewById(R.id.sample_btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissFragment();
            }
        });

        view.findViewById(R.id.sample_btn_dismiss_override_animation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overrideNextAnimation(R.anim.slide_out_to_bottom, R.anim.slide_in_from_top);
                dismissFragment();
            }
        });

        view.findViewById(R.id.sample_btn_dismiss_bundle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overrideNextAnimation(R.anim.slide_out_to_bottom, R.anim.slide_in_from_top);
                dismissFragment(new Bundle());
            }
        });

        view.findViewById(R.id.sample_btn_launch_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), TestIntentLaunchingActivity.class));
            }
        });

        view.findViewById(R.id.sample_btn_replace_root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                INavigationFragment fragmentAsNewRoot = SampleFragment.newInstance("This is a replaced root Fragment", 0);
                replaceRootFragment(fragmentAsNewRoot);
            }
        });

        return view;
    }

    public int getFragCount() {
        return mFragCount;
    }

    private class SampleModel implements Serializable {
        public String text1;
        public String text2;
        public String text3;
    }
}
