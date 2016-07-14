package com.sfuronlabs.ripon.tourbangla.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.sfuronlabs.ripon.tourbangla.FetchFromWeb;
import com.sfuronlabs.ripon.tourbangla.FileProcessor;
import com.sfuronlabs.ripon.tourbangla.PlaceAccessHelper;
import com.sfuronlabs.ripon.tourbangla.R;
import com.sfuronlabs.ripon.tourbangla.activities.DivisionListActivity;
import com.sfuronlabs.ripon.tourbangla.activities.SelectTourOperatorActivity;
import com.sfuronlabs.ripon.tourbangla.activities.SuggestNewPlaceActivity;
import com.sfuronlabs.ripon.tourbangla.activities.TourBlogActivity;
import com.sfuronlabs.ripon.tourbangla.activities.TourOperatorOffersListActivity;
import com.sfuronlabs.ripon.tourbangla.model.HomeFragmentElement;
import com.sfuronlabs.ripon.tourbangla.model.HomeFragmentImage;
import com.sfuronlabs.ripon.tourbangla.model.Place;
import com.sfuronlabs.ripon.tourbangla.util.Constants;
import com.sfuronlabs.ripon.tourbangla.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by amin on 6/27/16.
 */
public class HomeFragmentRecyclerAdapter extends RecyclerView.Adapter<HomeFragmentRecyclerAdapter.HomeFragmentViewHolder> {

    ArrayList<HomeFragmentElement> elements;
    Context context;
    ArrayList<HomeFragmentImage> homeFragmentImages1,homeFragmentImages2;
    SlideShowViewPagerAdapter slideShowViewPagerAdapter1,slideShowViewPagerAdapter2;
    public HomeFragmentRecyclerAdapter(final Context context, ArrayList<HomeFragmentElement> elements) {
        this.elements = elements;
        this.context = context;
        this.homeFragmentImages1 = new ArrayList<>();
        this.homeFragmentImages2 = new ArrayList<>();
        this.slideShowViewPagerAdapter1 = new SlideShowViewPagerAdapter(context,homeFragmentImages1);
        this.slideShowViewPagerAdapter2 = new SlideShowViewPagerAdapter(context,homeFragmentImages2);

        String url = Constants.FRONT_PAGE_IMAGE_LIST_URL;
        Log.d(Constants.TAG, url);

        FetchFromWeb.get(url,null,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray jsonArray = response.getJSONArray("content");
                    for (int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String category = jsonObject.getString("category");
                        String imagename = jsonObject.getString("imagename");
                        HomeFragmentImage homeFragmentImage = new HomeFragmentImage(id,category,imagename);
                        if (category.equals("browseplace")) {
                            homeFragmentImages1.add(homeFragmentImage);
                        } else if (category.equals("touroffer")) {
                            homeFragmentImages2.add(homeFragmentImage);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                slideShowViewPagerAdapter1.notifyDataSetChanged();
                slideShowViewPagerAdapter2.notifyDataSetChanged();
                Log.d(Constants.TAG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, statusCode+"failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public HomeFragmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlehomefragmentelement,parent,false);
        return new HomeFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HomeFragmentViewHolder holder, final int position) {

        HomeFragmentElement element = elements.get(position);
        holder.title.setText(element.getTitle());
        holder.button.setText(element.getButtonText());
        if (position == 0) {
            slideShowViewPagerAdapter1.setImageCategoty("browseplace");
            holder.viewPagerImageSlideShow.setAdapter(slideShowViewPagerAdapter1);
        } else if (position == 5) {
            slideShowViewPagerAdapter2.setImageCategoty("touroffer");
            holder.viewPagerImageSlideShow.setAdapter(slideShowViewPagerAdapter2);
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    boolean exists = false;
                    String[] files = context.fileList();
                    for (String file : files) {
                        if (file.equals("data.txt")) {
                            exists = true;
                            break;
                        } else {
                            exists = false;
                        }
                    }

                    if (!exists) {
                        String url = Constants.FETCH_PLACES_URL;
                        Log.d(Constants.TAG, url);
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Please wait...this may take a while...");
                        progressDialog.setTitle("Loading data");
                        progressDialog.show();

                        FetchFromWeb.get(url,null,new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                progressDialog.dismiss();
                                FileProcessor fileProcessor = new FileProcessor(context);
                                fileProcessor.writeToFile(response.toString());
                                Intent i = new Intent(context, DivisionListActivity.class);
                                context.startActivity(i);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                progressDialog.dismiss();
                                Toast.makeText(context, statusCode+"failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        FileProcessor fileProcessor = new FileProcessor(context);
                        fileProcessor.readFileAndProcess();
                        Intent i = new Intent(context, DivisionListActivity.class);
                        context.startActivity(i);
                    }

                }  else if (position == 1) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                    builderSingle.setIcon(R.drawable.ic_profile);
                    builderSingle.setTitle("Select One Name:-");
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            context,
                            android.R.layout.select_dialog_singlechoice);
                    arrayAdapter.add("DHAKA");
                    arrayAdapter.add("CHITTAGONG");
                    arrayAdapter.add("RAJSHAHI");
                    arrayAdapter.add("KHULNA");
                    arrayAdapter.add("SYLHET");
                    arrayAdapter.add("BARISAL");
                    arrayAdapter.add("RANGPUR");
                    builderSingle.setNegativeButton("cancel",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    builderSingle.setAdapter(arrayAdapter,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String strName = arrayAdapter.getItem(which);
                                    Intent i = new Intent("android.intent.action.HOTELS");
                                    i.putExtra("place", strName);
                                    context.startActivity(i);

                                }
                            });
                    builderSingle.show();

                } else if (position == 2) {
                    Intent i = new Intent(context, SelectTourOperatorActivity.class);
                    context.startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(context, TourBlogActivity.class);
                    context.startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(context, SuggestNewPlaceActivity.class);
                    context.startActivity(i);
                } else if (position == 5) {
                    Intent intent = new Intent(context, TourOperatorOffersListActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public static class HomeFragmentViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected Button button;
        protected ViewPager viewPagerImageSlideShow;
        protected LinearLayout linearLayout;

        public HomeFragmentViewHolder(View itemView) {
            super(itemView);

            title = ViewHolder.get(itemView, R.id.tourTitle);
            button = ViewHolder.get(itemView, R.id.button2);

            linearLayout = ViewHolder.get(itemView,R.id.cardcontainer);
            viewPagerImageSlideShow = ViewHolder.get(itemView,R.id.viewPagerImageSlideShow);
        }
    }
}