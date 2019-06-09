package com.ottice.ottice;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ottice.ottice.adapters.Load_review_adapter;
import com.ottice.ottice.models.ProvidersBeanClass;
import com.ottice.ottice.models.ReviewRatingBeanClass;
import com.ottice.ottice.utils.RecyclerViewItemMargin;

import java.util.ArrayList;

public class LoadReviewsActivity extends AppCompatActivity {

    private RecyclerView review_recyclerview;
    private Context context;
    private Load_review_adapter review_adapter;
    private ArrayList<ReviewRatingBeanClass> data=new ArrayList<>();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_reviews);

        init();

        data = getIntent().getParcelableArrayListExtra("list");
        if (data!=null) {
            setloadReviewRecyclerView();
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        context=this;
        review_recyclerview=(RecyclerView)findViewById(R.id.load_review_recyclerView);
        textView=(TextView)findViewById(R.id.no_reviews_text);
    }


    private void setloadReviewRecyclerView() {

        review_recyclerview.setHasFixedSize(false);
        review_recyclerview.setLayoutManager(new LinearLayoutManager(context));
        review_recyclerview.addItemDecoration(new RecyclerViewItemMargin(getResources().getDimensionPixelSize(R.dimen.dp_5),
                getResources().getDimensionPixelSize(R.dimen.dp_5),
                0,
                getResources().getDimensionPixelSize(R.dimen.dp_5)));
        review_adapter = new Load_review_adapter(context, data);
        review_recyclerview.setAdapter(review_adapter);


    }
}
