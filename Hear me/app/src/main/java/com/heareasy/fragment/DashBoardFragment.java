package com.heareasy.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.anychart.AnyChartView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.heareasy.R;
import com.heareasy.activity.DashBoardActivity;
import com.heareasy.common_classes.API_LIST;
import com.heareasy.common_classes.MethodFactory;
import com.heareasy.common_classes.MyStateView;
import com.heareasy.common_classes.SessionManager;
import com.heareasy.model.AudioRecordingDateModel;
import com.heareasy.model.GraphResponser;
import com.heareasy.model.RecordingReportModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.heareasy.activity.AudioRecordingListActivity.getDuration;
import static com.heareasy.activity.DashBoardActivity.mbottomNavigationView;


public class DashBoardFragment extends Fragment implements MyStateView.ProgressClickListener {
    private static final String TAG = "DashBoardFragment";
    private BarChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    private TextView tv_emptyGraph, tvFileCount;
    private AlertDialog dialog;
    private FloatingActionButton faBtn_dashboard;
    FloatingActionButton faBtn_start_with_wire, faBtn_start_with_wireless;
    TextView tv_start_with_wire, tv_start_with_wireless;
    Boolean isAllFabsVisible;
    private static final String[] DAYS = {"3 sept", "4 sept", "5 sept", "6 sept", "7 sept", "8 sept", "9 sept"};
    private static final String[] hours = {"", "1 hr", "2 hrs", "3 hrs", "4 hrs"};
    private static final int MAX_X_VALUE = 7;
    private static final int MAX_Y_VALUE = 50;
    private static final int MIN_Y_VALUE = 5;
    private MyStateView mLoadingBar;
    private SessionManager sessionManager;
    private AnyChartView anyChartView;
    String result;
    private ArrayList<BarEntry> barEnteryList;
    private ArrayList<String> labelNames;
    private ArrayList<String> labelYAxix;
    private ArrayList<File> fileCount;
    private ArrayList<AudioRecordingDateModel> datelist;
    private ArrayList<GraphResponser> graphList;
    private ArrayList arrayList;
    private GraphResponser graphModel;
    private String newDate = "";
    private String newTime = "";
    private File directory;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sessionManager = new SessionManager(getActivity());
        view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        mLoadingBar = new MyStateView(getActivity(), view);
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mbottomNavigationView.getMenu().findItem(R.id.bnav_home).setChecked(true);
        init();
    }


    private void init() {
//        anyChartView = view.findViewById(R.id.any_chart_view);
        chart = view.findViewById(R.id.BarChart);
        tv_emptyGraph = view.findViewById(R.id.tv_emptyGraph);
        tvFileCount = view.findViewById(R.id.tvFileCount);
        faBtn_dashboard = view.findViewById(R.id.faBtn_dashboard);
//        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        datelist = new ArrayList();
        arrayList = new ArrayList();
        graphList = new ArrayList();

        // Get file count from storage
        fileCount = new ArrayList<>();

        try {


            directory = ContextCompat.getExternalFilesDirs(getActivity(), Environment.DIRECTORY_MUSIC)[0];

            if (directory.listFiles() != null) {
                File[] files = directory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    String name = files[i].getName();
                    if (name.contains(".mp3")) {
                        fileCount.add(files[i]);
                        Date lastModDate = new Date(files[i].lastModified());
                        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(lastModDate);
                        String duration = getDuration(files[i]);
                        datelist.add(new AudioRecordingDateModel(duration, date));
                    }
                }
            }
            tvFileCount.setText("Files: " + fileCount.size());

            Log.e("createJSON", "createJSON:>>>>>>>>>>>>>>" + createJSON().length());
            if (createJSON().length() > 0)
                updateGraph();


            faBtn_dashboard.setOnClickListener(view1 -> {
//            callGraphDataAPI();
                if (createJSON().length() > 0)
                    updateGraph();
            });

            isAllFabsVisible = false;
            DashBoardActivity.tv_toolbar_tittle.setText("DashBoard");
        } catch (Exception e) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("akash",">>>>>>");
    }

    private void callGraphDataAPI() {
        mLoadingBar.showLoading();
        AndroidNetworking.get(API_LIST.RECOEDING_REPORT)
                .addHeaders("Authorization", sessionManager.getAuthToken())
                .addHeaders("id", sessionManager.getUserID())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "onResponse:>>>>>>>>>" + response.toString());
                        Gson gson = new Gson();
                        RecordingReportModel reportModel = gson.fromJson(response.toString(), RecordingReportModel.class);
                        try {
                            if (response.getString("responseCode").equalsIgnoreCase("200")) {
                                mLoadingBar.showContent();
//                            updateGraph(reportModel);
//                            Toast.makeText(getActivity(), "" + response.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                            } else if (response.getString("responseCode").equalsIgnoreCase("401")) {
                                mLoadingBar.showContent();
                                MethodFactory.forceLogoutDialog(getActivity());
                            } else {
                                mLoadingBar.showContent();
                                chart.setVisibility(View.INVISIBLE);
                                tv_emptyGraph.setVisibility(View.VISIBLE);
//                            Toast.makeText(getActivity(), "" + response.getString("responsemessage"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e(TAG, "onError:>>>>" + error.getMessage());
                        mLoadingBar.showRetry();
                    }
                });
    }


    private void updateGraph() {
        barEnteryList = new ArrayList<>();
        labelNames = new ArrayList<>();
        labelYAxix = new ArrayList<>();

        for (int i = createJSON().length() - 6; i <= createJSON().length(); i++) {
//            int total_recording_time = 0;
            String recording_date = "";
            try {
                JSONObject obj = createJSON().getJSONObject(i);
                recording_date = obj.getString("date");
                double oldTime = Double.parseDouble(obj.getString("time"));
                int total_recording_time = (int) oldTime;
                Log.e(TAG, "oldTime:>>>>>>>>>>>>>>" + total_recording_time);



           /* SimpleDateFormat in = new SimpleDateFormat("yyyy-MMMM-dd");

            Date date = null;
            try {
                date = in.parse(recording_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            result = DateFormat.getDateInstance().format(date);*/
                String[] resultDate = recording_date.split(",");

//            Log.e(TAG, "date:>>>>>>>>>> " + resultDate[0] + " " + resultDate[1]);

                Log.e(TAG, "recording_date: >>>>>>>" + recording_date);
                Log.e(TAG, "total_recording_time: >>>>>>>" + total_recording_time);

//            data.add(new ValueDataEntry(resultDate[0], total_recording_time));
                if (total_recording_time > 0) {
                    barEnteryList.add(new BarEntry(i, total_recording_time));
                }
                labelNames.add(resultDate[0]);
                labelYAxix.add(total_recording_time + " Min");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        BarDataSet barDataSet = new BarDataSet(barEnteryList, "");
        BarData barData = new BarData(barDataSet);
        chart.setData(barData);
        chart.setFitBars(true);
        chart.getData().notifyDataChanged();
        chart.notifyDataSetChanged();


        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());
        xAxis.setDrawGridLines(false);

        YAxis rightYAxis = chart.getAxisRight();
        rightYAxis.setEnabled(false);

        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);
        chart.setScaleEnabled(false);
//        chart.getLegend().setEnabled(false);
        LegendEntry legendEntryA = new LegendEntry();
        legendEntryA.label = "Data is shown as cumulative recorded minutes";
        legendEntryA.formColor = getResources().getColor(R.color.colorPrimary);
        chart.getLegend().setCustom(Arrays.asList(legendEntryA));
        chart.invalidate();
        chart.refreshDrawableState();
    }

    @Override
    public void onRetryClick() {

    }

    public JSONArray createJSON() {
        JSONArray newjArry = new JSONArray();
        for (int i = 0; i < datelist.size(); i++) {
            JSONObject jObjd = new JSONObject();
            if (!arrayList.contains(datelist.get(i).getDate())) {
                try {
                    jObjd.put("date", datelist.get(i).getDate());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrayList.add(datelist.get(i).getDate());

                JSONArray childjArry = new JSONArray();
                for (int k = 0; k < datelist.size(); k++) {
                    JSONObject childObjd = null;
                    if (datelist.get(k).getDate().contains(datelist.get(i).getDate())) {
                        childObjd = new JSONObject();
                        try {
                            childObjd.put("duration", datelist.get(k).getDuration());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (datelist.get(k).getDate().contains(datelist.get(i).getDate())) {
                        childjArry.put(childObjd);
                    }
                }
                try {
                    double sumDuration = 0.0;
                    for (int k = 0; k < childjArry.length(); k++) {
                        JSONObject jsonObject = childjArry.getJSONObject(k);
                        Log.e("time", ">>>>>>>>>>" + jsonObject.getString("duration"));
                        String time = jsonObject.getString("duration"); //mm:ss
                        String[] units = time.split(":"); //will break the string up into an array
                        double minutes = Integer.parseInt(units[1]); //first element
                        double seconds = Integer.parseInt(units[2]); //second element
                        double hours = Integer.parseInt(units[0]); //second element
                        double duration = minutes + seconds / 60 + hours * 60;
                        Log.e("duration", ">>>>>>>>>" + duration);
                        sumDuration += duration;

                    }
                    Log.e("final", ">>>>>>>>>" + sumDuration);
                    jObjd.put("time", sumDuration);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("JsonObject", jObjd.toString());
                newjArry.put(jObjd);
            }
        }
        arrayList.clear();
        Log.e("JsonChildArray", newjArry.toString());
        return newjArry;
    }


}

