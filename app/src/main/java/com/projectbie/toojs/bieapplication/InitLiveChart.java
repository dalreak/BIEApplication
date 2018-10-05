package com.projectbie.toojs.bieapplication;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.projectbie.toojs.bieapplication.ConClient;

import java.util.List;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Integer.parseInt;

public class InitLiveChart {

    Context chartcontext;
    List<Feed> test;

    private LineChart tempchart;
    private LineChart humchart;
    private LineChart wlevchart;
    private LineChart swlevchart;
   /*   쓰레드 수정중
    private Thread tempthread;
    private Thread humthread;
    private Thread wlevthread;
    private Thread swlevthread;
    */
   private Thread updatedata;

    public InitLiveChart(Context context){

        chartcontext = context;

        tempchart = (LineChart) ((MainActivity) chartcontext).findViewById(R.id.tempchart);
        humchart = (LineChart) ((MainActivity) chartcontext).findViewById(R.id.humchart);
        wlevchart = (LineChart) ((MainActivity) chartcontext).findViewById(R.id.wlevchart);
        swlevchart = (LineChart) ((MainActivity) chartcontext).findViewById(R.id.swlevchart);
       /* 쓰레드 수정중
        initchart(tempchart,tempthread,"temperature");
        initchart(humchart,humthread,"humidity");
        initchart(wlevchart,wlevthread,"water-level");
        initchart(swlevchart,swlevthread,"soil-water-level");
       */
       InitClient initclient = new InitClient(); //수정중
        initclient.getjsonData(context);

        String a = responsebody.;
       String chartname[]={"temperature","humidity","water-level","soil-water-level"};
        initchart(tempchart,humchart,wlevchart,swlevchart,updatedata,chartname,initclient,chartcontext);


    }

    private void initchart(LineChart temp,LineChart hum,LineChart wlev,LineChart swlev,Thread thname,String chartname[],InitClient clientname,Context context){
        /*  수정중
        //tempchart = (LineChart) findViewById(R.id.tempchart);

        //아래의 Axis 설정
        XAxis xAxis = name.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //xAxis 위치 아래쪽
        xAxis.setTextSize(10f);                         //xAxis 표출되는 텍스트 크기 10f
        xAxis.setDrawGridLines(false);                  //xAxis 그리드 라인 없앰

        //왼쪽의 Axis 설정
        YAxis leftAxis = name.getAxisLeft();
        leftAxis.setDrawGridLines(false); //leftAxis 의 그리드 라인 없앰

        //오른쪽의 Axis 설정
        YAxis rightAxis = name.getAxisRight();
        rightAxis.setEnabled(false); //rightAxis 비활성화

        LineData data = new LineData();
        name.setData(data); //LineData 를 셋팅함
        */


        firstinitchart(temp,chartname[0],clientname,context);
        //firstinitchart(hum,chartname[1],clientname,context);
        //firstinitchart(wlev,chartname[2],clientname,context);
        //firstinitchart(swlev,chartname[3],clientname,context);

        feedMultiple(thname,temp,hum,wlev,swlev,chartname); //쓰레드를 활용하여 실시간으로 데이터 주입
    }

    private void feedMultiple(Thread name,final LineChart temp,final LineChart hum,final LineChart wlev,final LineChart swlev,final String chartname[]){
        if(name != null)
            name.interrupt(); //동작중인 쓰레드에 interrupt 검

        final  Runnable runnable = new Runnable() {
            @Override
            public void run() {

                addEntry(temp,chartname[0]); //addentry 실행
                addEntry(hum,chartname[1]); //addentry 실행
                addEntry(wlev,chartname[2]); //addentry 실행
                addEntry(swlev,chartname[3]); //addentry 실행
            }
        };

        name = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    ((MainActivity) chartcontext).runOnUiThread(runnable);    //UI 쓰레드 위에 생성한 runnable 실행
                    try{
                        Thread.sleep(100); // 0.1 초 쉼
                    }catch(InterruptedException ie){
                        ie.printStackTrace();
                    }
                }
            }
        });
        name.start();

    }

    private void addEntry(LineChart name,String chartname){
        LineData data = name.getData();  // onCreate에서 생성한 LineData를 가져옴
        if(data != null)                    // 데이터가 비어있지 않으면
        {
            ILineDataSet set = data.getDataSetByIndex(0); //0번 위치의 데이터셋 가져옴

            if(set == null){
                set = createSet(chartname); //createSet 실행
                data.addDataSet(set); //createSet 실행한 set을 데이터셋에 추가
                //for(int i=0;i<movieList)

            }

            //set의 맨 마지막에 랜덤값 (30~69.99999) 을 Entry로 data 에 추가함
            data.addEntry(new Entry(set.getEntryCount(), (float)(Math.random() * 40) + 30f), 0);
            data.notifyDataChanged();        // data의 값 변동을 감지함

            name.notifyDataSetChanged();                // chart의 값 변동을 감지함
            name.setVisibleXRangeMaximum(100);            // chart에서 최대 X좌표기준으로 몇개의 데이터를 보낼지 정함
            name.moveViewToX(data.getEntryCount());    // 가장 최근에 추가한 데이터의 위치로 chart를 이동함
        }
    }

    private LineDataSet createSet(String chartname) {

        LineDataSet set = new LineDataSet(null,chartname); //데이터셋의 이름을Dynamic Data으로 설정 기본값은 null
        set.setAxisDependency(YAxis.AxisDependency.LEFT); //Axis를 YAxis 의 LEFT 를 기본으로 설정
        set.setColor(ColorTemplate.getHoloBlue()); //데이터의 라인색을 HoloBlue 설정
        set.setCircleColor(Color.WHITE); //데이터의 점을 하얀색으로 설정
        set.setLineWidth(2f); //그래프 라인 두께를 2f로 설정
        set.setCircleRadius(2f); //그래프 점의 반지름을 4f로 설정
        set.setFillAlpha(65); //투명도 채우기를 65로 설정
        set.setFillAlpha(ColorTemplate.getHoloBlue()); //채우기 색을 HoloBlue로 설정
        set.setHighLightColor(Color.rgb(244,117,117)); //하이라이트 컬러변경
        set.setDrawValues(false); //각 데이터의 값을 텍스트로 나타내지 않게 설정
        return set; //데이터 set 반환
    }
    private void firstinitchart(LineChart name,String chartname,InitClient clientname,Context context){
        //아래의 Axis 설정
        XAxis xAxis = name.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //xAxis 위치 아래쪽
        xAxis.setTextSize(10f);                         //xAxis 표출되는 텍스트 크기 10f
        xAxis.setDrawGridLines(false);                  //xAxis 그리드 라인 없앰

        //왼쪽의 Axis 설정
        YAxis leftAxis = name.getAxisLeft();
        leftAxis.setDrawGridLines(false); //leftAxis 의 그리드 라인 없앰

        //오른쪽의 Axis 설정
        YAxis rightAxis = name.getAxisRight();
        rightAxis.setEnabled(false); //rightAxis 비활성화

        LineData data = new LineData();
        name.setData(data); //LineData 를 셋팅함

        LineData dataseter = name.getData();  // onCreate에서 생성한 LineData를 가져옴

        ILineDataSet set = dataseter.getDataSetByIndex(0); //0번 위치의 데이터셋 가져옴
        set = createSet(chartname); //createSet 실행
        data.addDataSet(set); //createSet 실행한 set을 데이터셋에 추가


        //clientname.getjsonData(context);
        //List<Feed> temp = clientname.getjsondata();
        //String a = test;
        //for(int i=5;i<10;i++){
            //data.addEntry(new Entry(set.getEntryCount(),5),0);
       // }

        /*
        data.notifyDataChanged();        // data의 값 변동을 감지함
        name.notifyDataSetChanged();                // chart의 값 변동을 감지함
        name.setVisibleXRangeMaximum(100);            // chart에서 최대 X좌표기준으로 몇개의 데이터를 보낼지 정함
        name.moveViewToX(data.getEntryCount());    // 가장 최근에 추가한 데이터의 위치로 chart를 이동함
        */
    }
}
